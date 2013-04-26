package com.stratosim.server;

import javax.servlet.ServletContext;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public class ConfigFactory
{
  private static Configuration configInstance = null; 
  private ConfigFactory() {} 
  public static Configuration get(ServletContext context) 
  { 
   if (configInstance == null)
   {
    configInstance = new Configuration();
    configInstance.setServletContextForTemplateLoading(context, "WEB-INF/templates");
    configInstance.setObjectWrapper(new DefaultObjectWrapper()); 
   }

   return configInstance; 
  }
}
