require: slotfilling/slotFilling.sc
    module = sys.zb-common
require: city/city.sc
    module = sys.zb-common
require: functions.js


theme: /

  state: Start
      q!: $regex</start>
      a: Здравствуйте! Я бот тех поддержки.
          Перед использованием давайте авторизируемся
      go!: /AskLogin
      
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

  state: GenerateReport
      intent!: /Reason
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
      a: {{$temp.resp.data.title}}
      script:
          $session.reportResult = formatReportData($temp.resp.data);
      a: {{$session.reportResult}}
      go!: /MainSpace

  state: MainSpace
      a: Я бот тех поддержки. Ты можешь попросить меня
                1) Создать отчеты 
                2) Проверить информацю 
                3) Задать вопрос по проекту
    #   buttons:
    #       "Отчеты" -> /Reports
    #       "Задать вопрос по проекту" -> /Ask

  state: Ask
      a: Можешь задать мне вопрос по проекту
      intent: /createReport || toState = "/Report_Anwer"
      intent: /findProblem || toState = "/Report_Anwer_1"
      event: noMatch || toState = "./"
  state: Report_Anwer
      a: Создать отчет можете в этом боте:
          Напишите создать ОТЧЕТ с ДАТА по ДАТА для конкретной проблемы. 
          
      buttons:
          "Создать отчет" -> /Reports
          "Вернуться назад" -> /MainSpace
      intent: /Reason || toState = "/GenerateReport"
      event: noMatch || toState = "./"
