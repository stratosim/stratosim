      <div class="row">
          <div class="span12">
             <h2>Examples</h2>
          </div>
      </div>

      <div class="row">
          <div class="span3">
            <div class="well sidebar-nav">
            <ul class="nav nav-list">
            
              <#if requestURI == "/examples" || requestURI == "/examples/rc_circuit">
                <li class="active"><a href="/examples/rc_circuit">RC Circuit</a></li>
              <#else>
                <li><a href="/examples/rc_circuit">RC Circuit</a></li>
              </#if>
              
              <#if requestURI == "/examples/rc_ac_response">
                <li class="active"><a href="/examples/rc_ac_response">RC AC Response</a></li>
              <#else>
                <li><a href="/examples/rc_ac_response">RC AC Response</a></li>
              </#if>
            
              <#if requestURI == "/examples/rlc_ac_response">
                <li class="active"><a href="/examples/rlc_ac_response">RLC AC Response</a></li>
              <#else>
                <li><a href="/examples/rlc_ac_response">RLC AC Response</a></li>
              </#if>
              
              <#if requestURI == "/examples/logic_gates">
                <li class="active"><a href="/examples/logic_gates">Logic Gates</a></li>
              <#else>
                <li><a href="/examples/logic_gates">Logic Gates</a></li>
              </#if>

              <#if requestURI == "/examples/cmos_or_gate">
                <li class="active"><a href="/examples/cmos_or_gate">CMOS OR Gate</a></li>
              <#else>
                <li><a href="/examples/cmos_or_gate">CMOS OR Gate</a></li>
              </#if>

            </ul>
            </div><!--/.well -->
          </div>

          <div class="span9">