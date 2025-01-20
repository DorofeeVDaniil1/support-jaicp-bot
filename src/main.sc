
theme: /

  state: Start
      q!: $regex</start>
      intent: /login || toState = "/Start"
      intent: /auth || toState = "/AskLogin"
      event: noMatch || toState = "./"

  state: AskLogin
      InputText: 
          prompt = Для начала работы мне нужно авторизоваться. Напишите ваш логин.
          varName = username
          html = 
          htmlEnabled = false
          then = /AskPassword
          actions = 

  state: AskPassword
      InputText: 
          prompt = Введите ваш пароль.
          varName = password
          html = 
          htmlEnabled = false
          then = /AskServers
          actions = 

  state: FetchRequest
      HttpRequest: 
          url = https://disp.t1.groupstp.ru/app/api/v1/authenticate
          method = POST
          dataType = 
          body = {"username":"sysadmin","password":"MOdSqw9S"}
          okState = /Answer
          timeout = 0
          headers = [{"name":"Content-Type","value":"application\/json"}]
          vars = [{"name":"id_token","value":"$httpResponse.id_token"}]
      event: noMatch || toState = "./"

  state: Answer
      intent!: /login
      go: AskPassword
      a: Все отлично

   
    state: AskServers
     InputText: 
          prompt = Введите ваш сервер.
          varName = servers
          html = 
          htmlEnabled = false
          then = /FetchRequest
          actions = 
