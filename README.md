# Строительная компания
Заказ услуг по сторительным работам.

###Роли:

####Клиент
1.  Заказывает работу.
1.  Принимает результат.
1.  Оплачивает работу.

####Менеджер
1.  Составляет смету + смету доработок (на основе списка доработок от Прораба).
1.  Ведёт учёт ресурсов со склада.(дозаказывает по мере надобности).
1.  Ведёт учёт бюджета компании.
1.  Принимает оплату от клиента.

####Прораб
1.  Получает список работ.
1.  Выполняет работу.
1.  Составляет список доработок.
1.  Отдаёт работу на приём Клиенту.

###Подробное описание вариантов использования
1.  Процесс оформления заказа.
прописываются все требуемые ресурсы и услуги оказываемые прорабом.
Если ресурсов на складе нехватает то происходит дозказ ресурсов.
2.  Процесс сдачи/приёма работы.

  **2 Варианта:**
    *  Первый. Успешная сдача обьекта клинт принимает работу прораба и получает смету (составленную менеджером) со списком проведённых работ.
    *  Второй. В случае если клиент требует доработки, прораб составляет список требуемых работ(или ресурсов) а менеджер составляет смету доработок,
	а дальше выполняютсся действия как в первом варианте или повторные доработки.
	В случае если доработок запорсов на доработку больше 2-ух составляется промежуточная (текущая смета, смета из всех проведённых работ требующих: смета + 1-ая смета доработок + 2-ая семта доработок ),
	пока она не будет оплачена на 85% (от суммы всего Заказа(заказ - это все сметы вместе) ) новые требования доработки от клиента не принимаются.
	
3.  Процесс оплаты счёта.
  **2 варианта** действий, клиент может оплатить:  
    * наличными или 
    * по безналичному расчёту.

###use-case диаграмма.
![use-case](/uml architect prog.png)

###uml диаграмма.
![uml-diagram](/диаграмма.png)
