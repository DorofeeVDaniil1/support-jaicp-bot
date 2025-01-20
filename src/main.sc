
theme: /

  state: Start
      q!: $regex</start>
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
          url = https://disp.{{$session.servers}}.groupstp.ru/app/api/v1/authenticate
          method = POST
          dataType = application/json
          body = {"username":"{{$session.username}}",
              "password":"{{$session.password}}"}
          okState = /Answer
          timeout = 0
          headers = [{"name":"Content-Type","value":"application\/json"}]
          vars = [{"name":"id_token","value":"$httpResponse.id_token"}]
          errorState = /Fail
      a: {{$session.id_token}}

  state: Answer
      a: Все отлично, вывожу токен:
      a: {{$session.data.id_token}}
      a: {{$session.password}}
      a: {{$session.username}}
      a: {{$session.httpStatus}}

  state: Fail
    a: Произошла ошибка при запросе токена.
    a: Неизвестная ошибка

  state: AskServers
      InputText: 
          prompt = Введите ваш сервер.
          varName = servers
          html = 
          htmlEnabled = false
          then = /newState
          actions = 
              
  state: newState
        script:
            $temp.response = $http.post(
                "https://disp.t1.groupstp.ru/app/api/v1/authenticate", 
                {
                    body: {
                        "username": $session.username,
                        "password": $session.password
                    },
                    headers: {
                        "Content-Type": "application/json"
                    }
                }
            );
        if: $temp.response.isOk
            script:
               
                  var monthsGenitive = [
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"
                    ];
            
              
            a: {{$temp.response.data.id_token}} 
            