package com.stratosim.server.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedInteger;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.stratosim.server.persistence.ObjectifyTestPersistenceLayer;
import com.stratosim.server.persistence.PersistenceLayerFactory;
import com.stratosim.server.persistence.objectify.OfyService;

public class ObjectifyTestServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
  private static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().trimResults();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {

    UserService userService = UserServiceFactory.getUserService();
    Preconditions.checkState(userService.isUserAdmin());
    
    ImmutableList<String> reqArgs;
    if (req.getPathInfo() == null) {
      reqArgs = ImmutableList.of();
    } else {
      reqArgs = FluentIterable.from(SPLITTER.split(req.getPathInfo()))
          .transform(new UnescapeFunction())
          .toImmutableList();
    }
    
    ObjectifyTestPersistenceLayer layer = PersistenceLayerFactory.getObjectifyTestLayer();

    resp.setContentType("text/html");
    switch (reqArgs.size()) {
      case 0:
        printEntityTypes(resp.getWriter());
        break;
      case 1:
        Class<?> entityClass = forNameUnchecked(reqArgs.get(0));
        printEntitiesByTypeUnchecked(resp.getWriter(), entityClass,
            layer.getAll(entityClass, UnsignedInteger.valueOf(20)));
        break;
      case 2:
        entityClass = forNameUnchecked(reqArgs.get(0));
        try {
          long id = Long.parseLong(reqArgs.get(1));
          printEntityUnchecked(resp.getWriter(), entityClass, layer.getById(entityClass, id));
        } catch (NumberFormatException e) {
          printEntityUnchecked(resp.getWriter(), entityClass, layer.getById(entityClass,
              reqArgs.get(1)));
        }
        break;
      default:
        throw new IllegalArgumentException("Too many arguments: " + reqArgs.size());
    }
  }
  
  private void printEntityTypes(PrintWriter out) {
    out.println("<html>");
    out.println("<head><title>Admin - objectify test - entities</title></head>");
    out.println("<body>");
    out.println("<table>");
    
    out.println("<tr>");
    out.println("<th>Entity</th>");
    out.println("</tr>");
    
    for (Class<?> entityClass : OfyService.ENTITY_CLASSES) {
      out.println("<tr>");
      out.printf("<td><a href=\"/a/objectify-test/%s\">%s</a></td>",
          StringEscapeUtils.escapeHtml(entityClass.getCanonicalName()),
          StringEscapeUtils.escapeHtml(entityClass.getSimpleName()));
      out.println("</tr>");
    }
    
    out.println("</table>");
    out.println("</body></html>");
  }
  
  @SuppressWarnings("unchecked")
  private static <T> void printEntitiesByTypeUnchecked(PrintWriter out, Class<?> entityClass,
                                                       ImmutableList<T> entities) {
    printEntitiesByType(out, (Class<T>) entityClass, entities);
  }
  
  private static <T> void printEntitiesByType(PrintWriter out, Class<T> entityClass,
                                              ImmutableList<T> entities) {
    Preconditions.checkState(entityClass.getAnnotation(Entity.class) != null,
        "Invalid entity class: " + entityClass.getName());
    
    String entityName = entityClass.getSimpleName();
    out.println("<html>");
    out.printf("<head><title>Admin - objectify test - Entities of type %s</title></head>",
        entityName);
    out.println("<body>");

    printEntitiesTable(out, entityClass, entities);
    
    out.println("</body></html>");
  }
  
  @SuppressWarnings("unchecked")
  private static <T> void printEntityUnchecked(PrintWriter out, Class<?> entityClass, T entity) {
    printEntity(out, (Class<T>) entityClass, entity);
  }
  
  private static <T> void printEntity(PrintWriter out, Class<T> entityClass, T entity) {
    String entityName = entity.getClass().getSimpleName();
    out.println("<html>");
    out.printf("<head><title>Admin - objectify test - Entity of type %s</title></head>",
        entityName);
    out.println("<body>");

    printEntitiesTable(out, entityClass, ImmutableList.of(entity));
    
    out.println("</body></html>");
  }
  
  private static <T> void printEntitiesTable(PrintWriter out, Class<T> entityClass,
                                             ImmutableList<T> entities) {
    out.println("<table>");
    
    Field idField = null;
    List<Field> entityFields = Lists.newArrayList();
    Iterable<Field> fields = Iterables.concat(
        Arrays.asList(entityClass.getDeclaredFields()),
        Arrays.asList(entityClass.getSuperclass().getDeclaredFields()));
    for (Field field : fields) {
      Annotation idAnnotation = field.getAnnotation(Id.class);
      if (idAnnotation != null) {
        Preconditions.checkState(idField == null, "can only have one @Id-annotated field");
        idField = field;
      } else {
        entityFields.add(field);
      }
    }
    Preconditions.checkState(idField != null, "must have one @Id-annotated field");
    
    out.println("<tr>");
    out.printf("<th>@Id %s</th>", StringEscapeUtils.escapeHtml(idField.getName()));
    for (Field field : entityFields) {
      out.printf("<th>%s</th>", StringEscapeUtils.escapeHtml(field.getName()));
    }
    out.println("</tr>");

    for (T entity : entities) {
      out.println("<tr>");
      String idString = String.valueOf(getFieldUnchecked(idField, entity));
      out.printf("<td><a href=\"/a/objectify-test/%s/%s\">%s</a></td>",
          StringEscapeUtils.escapeHtml(entityClass.getCanonicalName()),
          StringEscapeUtils.escapeHtml(idString),
          StringEscapeUtils.escapeHtml(idString));
      for (Field field : entityFields) {
        out.printf("<td>%s</td>",
            StringEscapeUtils.escapeHtml(String.valueOf(getFieldUnchecked(field, entity))));
      }
      out.println("</tr>");
    }

    out.println("</table>");
  }
  
  private static Object getFieldUnchecked(Field field, Object o) {
    try {
      return field.get(o);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }
  
  private static Class<?> forNameUnchecked(String className) {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  private static class UnescapeFunction implements Function<String, String> {
    @Override
    public String apply(String htmlEscaped) {
      return StringEscapeUtils.unescapeHtml(htmlEscaped);
    }
  }
}



