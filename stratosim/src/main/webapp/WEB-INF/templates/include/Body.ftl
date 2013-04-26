<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-35021246-1']);
  _gaq.push(['_setDomainName', 'stratosim.com']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>

</head>
<body>
    <div class="navbar">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="/"> <h1 class="title">StratoSim</h1> </a>
          <div class="nav-collapse">
            <ul class="nav">
              
              <#if topLevel == "">
                <li class="active"><a href="/">Home</a></li>
              <#else>
                <li><a href="/">Home</a></li>
              </#if>
              
              <#if topLevel == "tutorial">
                <li class="active"><a href="/tutorial">Tutorial</a></li>
              <#else>
                <li><a href="/tutorial">Tutorial</a></li>
              </#if>  
              
              <#if topLevel == "examples">
                <li class="active"><a href="/examples">Examples</a></li>
              <#else>
                <li><a href="/examples">Examples</a></li>
              </#if>  

              <li><a href="mailto:contact@stratosim.com" target="_blank">Contact</a></li>
              
              <#if topLevel == "app">
                <li class="active"><a class="blue" href="/app/">Sign In</a></li>
              <#else>
                <li><a class="blue" href="/app/">Sign In</a></li>
              </#if>  

            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

    <div class="container">
