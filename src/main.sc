require: slotfilling/slotFilling.sc
    module = sys.zb-common

theme: /

  state: Start
      q!: $regex</start>
      intent: /auth || toState = "/AskLogin"
      intent: /Прив || toState = "./"
      intent: /Отчет || toState = "/Reports"
      event: noMatch || toState = "./"
      a: Привет!
            Я бот тех поддержки проекта Чистая Логистика
            Можешь задать вопрос который тебя интресует
      buttons:
          "Кнопка 1"

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
          then = /newState
          actions = 

  state: Answer
      a: Все отлично, вывожу токен:
      a: {{$session.token_id}}

  state: Fail
    a: Произошла ошибка при запросе токена.
    a: Неизвестная ошибка

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
              $session.token_id = $temp.response.data.id_token;
          go!: /Answer
      else: 
          go!: /Fail

  state: Reports
      a: Выбери отчет, который хочешь создать
      buttons:
          "Отчет о невывозе"
          "Отчет о новых и пренесенных ТС"
          
  state: Example
    intent!: /Reason
    script:
        if ($parseTree._RemovailProblems) {
            $temp.problem = $parseTree._RemovailProblems.problem;
        } else {
            $temp.problem = "Биологические отходы";
        }
    a: Погода в {{$parseTree._City}} на {{$temp.date}}