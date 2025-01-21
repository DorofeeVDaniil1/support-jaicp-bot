require: slotfilling/slotFilling.sc
    module = sys.zb-common
require: city/city.sc
    module = sys.zb-common

theme: /

  state: Start
      q!: $regex</start>
      a: Здравствуйте! Я бот тех поддержки.
          Перед использованием давайте авторизируемся
      go!: /AskLogin
      
  state: test
      q!: @RemovalFail
      a: {{$parseTree._RemovalFail.problem}}

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
      go!: /MainSpace

  state: Fail
      a: Произошла ошибка при запросе токена.
      a: Неизвестная ошибка
      go!: /AskLogin

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
          "Отчет о невывозе" -> /Reports
          "Отчет о новых и пренесенных ТС"

  #   state: Example
  #     intent!: /Reason
  #     script:
  #         if ($parseTree._RemovailProblems) {
  #             $temp.problem = $parseTree._RemovailProblems.problem;
  #         } else {
  #             $temp.problem = "Биологические отходы";
  #         }
  #     a: Погода в {{$parseTree._City}} на {{$temp.date}}
  state: RemovalFailReport
      intent!: /Reason
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
          script:
                $session.token_id = $temp.response.data.id_token;
          go!: /Answer
      go!: /RemovalFailReport

  state: MainSpace
      a: Я бот тех поддержки. Ты можешь попросить меня
          1) Создать отчеты 
          2) Проверить информацю 
          3) Задать вопрос по проекту
      buttons:
          "Отчеты"
          "Проверить информацию"
          "Задать вопрос по проекту"
          
  state: Information
      a: Я бот тех поддержки. Ты можешь попросить меня
          1) Создать отчеты 
          2) Проверить информацю 
          3) Задать вопрос по проекту
      buttons:
          "Отчеты"
          "Проверить информацию"
          "Задать вопрос по проекту"