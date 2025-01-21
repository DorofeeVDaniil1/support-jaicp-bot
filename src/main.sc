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
              $session.token_id = "Bearer " + $temp.response.data.id_token;
          go!: /Answer
      else: 
          go!: /Fail

  state: Reports
      a: Выбери отчет, который хочешь создать
      intent: /Reason || toState = "/GenerateReport"
      event: noMatch || toState = "./"

  #   state: Example
  #     intent!: /Reason
  #     script:
  #         if ($parseTree._RemovailProblems) {
  #             $temp.problem = $parseTree._RemovailProblems.problem;
  #         } else {
  #             $temp.problem = "Биологические отходы";
  #         }
  #     a: Погода в {{$parseTree._City}} на {{$temp.date}}
  state: GenerateReport
      intent!: /Reason
      a:$session.token_id
      script:
          $session.reportName = $parseTree._ReportName.name;
          $session.datefrom = $parseTree._Interval.from.value;
          $session.dateto = $parseTree._Interval.to.value;
          $session.problemId = $parseTree._Problems.id;
      script:
          $temp.resp = $http.post(
            "https://disp.t1.groupstp.ru/app/api/v1/service/reports/generate", 
            {
                body: {
                "reportName": $session.reportName,
                "parameters": {
                    "dateFrom": $session.datefrom,
                    "dateTo": $session.dateto,
                    "allContainersNotRemoved": true,
                    "removalFailByReportFromDriver": true,
                    "removalFailAreaNotInTask": false,
                    "removalProblemIds": $session.problemId,
                    "removalFailDetailsWithoutReport": false
                },
                "outputFormat": "json"
                },
                headers: {
            "Content-Type": "application/json",
            "Authorization": $session.token_id
                },
                timeout: 30000
            }
                );
     a:Всё окей
  state: MainSpace
      a: Я бот тех поддержки. Ты можешь попросить меня
            1) Создать отчеты 
            2) Проверить информацю 
            3) Задать вопрос по проекту
      buttons:
          "Отчеты" -> /Reports
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