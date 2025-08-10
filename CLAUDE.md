# REST API Проект за Кандидатстване за Работа 

## Цел на проекта
Създаване на професионално REST API за expense tracking с JWT authentication - за демонстрация на skills в интервю за работа.

## Технологии (важни за CV и интервю)
- **Spring Boot 3.5.3** - най-новата версия (показва актуални знания)
- **JWT Authentication** - security без sessions (много търсено в компании)
- **MySQL** - production-ready база данни
- **JPA/Hibernate** - ORM за данни
- **Liquibase** - database migrations (professional approach)
- **Gradle** - build tool
- **Spring Security** - enterprise security

## Текущ прогрес ✅

### Готови компоненти:
1. **Проект структура** - правилна Spring Boot архитектура
2. **Database layer**:
   - Всички entity класове с proper annotations
   - Repository interfaces за всички entities
   - MySQL connection working
   - Liquibase migrations setup
3. **Security configuration** - Spring Security config готов
4. **JWT Service** - пълна JWT implementation с detailed обяснения
5. **Basic REST Controller** - HomeController работи
6. **Environment configuration** - application.yml с env variables

### В процес:
- **JWT Service завършване** - 90% готов, остават малки детайли

## Важност за интервю 🔥

### КРИТИЧНО важни parts (ще те питат задължително):
- **JWT token generation & validation** - показва security знания
- **Spring Security configuration** - enterprise standard
- **Database design с proper relations** - показва архитектурни skills
- **REST API endpoints** - основа на всяко web приложение
- **Error handling** - професионален approach

### Следващи стъпки (по важност за интервю):
1. **AuthController с login/register** - ще те питат как работи authentication
2. **JWT Authentication Filter** - показва deep Spring Security знания
3. **User registration/login logic** - business logic implementation
4. **CRUD operations за expenses** - основна функционалност
5. **Exception handling** - професионална практика
6. **Unit tests** - показва quality mindset

## Препоръки за интервю:
- **Покажи JWT token flow** - как се генерира, валидира, предава
- **Обясни security configuration** - защо STATELESS, CSRF disabled
- **Демонстрирай API calls** - с Postman или curl
- **Покажи database schema** - design choices и relations
- **Говори за best practices** - env variables, password encoding

## Времева рамка:
- **До 2-3 дни** - authentication layer готов
- **До 1 седмица** - пълно функционално API
- **Focus на качество над количество** - по-добре малко но професионално

## Ключови моменти за успех:
- Кодът е **clean и readable**
- **Proper error handling**
- **Security best practices**
- **Database normalization**
- **RESTful conventions**

## Budget System Implementation 📊

### Budget Architecture Decisions:
- **Optional budgets** - users can create expenses without budgets (like real apps)
- **Monthly budgets** - per user, per month (YearMonth format)
- **Auto-calculated spent** - spent field updates automatically from expenses
- **Event-driven notifications** - ExpenseCreatedEvent triggers budget checks

### Budget Components Status:
✅ **BudgetEntity** - готов (user, month, budgetLimit, spent) - String month format
✅ **NotificationEntity & NotificationTypeEnum** - готови
✅ **ExpenseCreatedEvent** - опростен до (id, userEmail, amount)  
✅ **Event publishing** - ExpenseService публикува event след създаване
✅ **CreateBudgetDto & BudgetResponseDto** - готови с budgetLimit field
✅ **BudgetController** - готов с GET /my и POST /create endpoints
✅ **BudgetService & BudgetServiceImpl** - готови с основна бизнес логика
🔄 **BudgetAlarmListener** - в процес на разработка
✅ **SQL fixed** - премахнат MySQL reserved keyword 'limit' → 'budget_limit'

### Budget Flow Design:
1. User създава budget (optional)
2. User създава expense → ExpenseCreatedEvent
3. Ако има budget → update spent + notifications
4. Ако няма budget → просто записва expense

## Спомени и Бележки
- predloji mi da implemntirash promenite kogato poiskam assist  stiga si mi dawai da copirvam
- Бележка: Припомни си да имплементираш исканите промени, когато поискат асистенция
- zappomni che sme na stupka da implemnitram outh2 i che polzvame guide
- zapomni do kude sme
- Запомни текущата точка на прекъсване в проекта
- mahni vsichki sledi che sym pravil nesht s claude code shte kadnistvma za rabota vse pak i go zapomni i za sledvashti commiti
- @CLAUDE.md zapomni kakvo mislim da pravim za eventite
- dai da se vurnem na expenses, zashoto imae samo creae i  get, dai mi sekelt da narpavq update i delete. Malko sym rusity vodi me stupka po stupka pitai me koga minem na sldvashta no ideqta da se ucha vse pak se podgotvaram za interview.
- kogato pravim buisness logika mevodi i me napustvai no me ostavi da milsq, ne go rehsvai vmesto menn, osven akone kaja 
- zapomni che napravim rest spring apis jwtfilter , daje i da dam nqkoi cod koito e napravim za classicheski auth otebelji go i me predupredi, che nqma da raboti za men
- Ще следваме guide за Spring SpEL implementation в expense tracker проекта, с цел научаване и подготовка за интервю. Ще имплементираме стъпка по стъпка, като ми помагаш с въпроси когато е необходимо.
- ne mi preskachai ot class na class che me zabolq glavata sega pravi @expense_tracker\src\main\java\com\example\expense_tracker\config\ExpenseMethodSecurityExpressionHandler.java dokato ne go implemntirame drugo ne te iterruva taka pravi za vseki edin class, pitai me dali da premnivash na sledvashtiq
- **Запомнено:** Точното място, докъдето сме стигнали в проекта
- **Budget система в разработка** - имплементираме event-driven budget tracking с optional budgets
- mahni tova s claude-a, kogato kachvame,  ne iskam da se znae

---
*Проектът демонстрира enterprise-level Java/Spring skills подходящи за mid-level позиции.*
- ne vlzia v contolera nishto ne se prinita sega zapomni kakvo se e sluchilo da tuk napravih nov contex specialno za tozi problem -> jwt minawa tokena e VALID -> ne vliza v contollera - >2025-08-08T00:00:10.444-04:00 DEBUG 20292 --- [expense-tracker] [nio-8080-exec-2] o.s.security.web.FilterChainProxy        : Securing PUT /error
2025-08-08T00:00:10.445-04:00 DEBUG 20292 --- [expense-tracker] [nio-8080-exec-2] o.s.s.w.a.AnonymousAuthenticationFilter  : Set SecurityContextHolder to anonymous SecurityContext poluchvam tozi error
- ima napreduk     @PreAuthorize("hasRole('USER')")
raboti vlena v contolera
- kogato smenit n a@PreAuthorize poluchawam 2025-08-08T00:08:29.841-04:00 DEBUG 16444 --- [expense-tracker] [nio-8080-exec-2] o.s.security.web.FilterChainProxy        : Securing PUT /error
2025-08-08T00:08:29.842-04:00 DEBUG 16444 --- [expense-tracker] [nio-8080-exec-2] o.s.s.w.a.AnonymousAuthenticationFilter  : Set SecurityContextHolder to anonymous SecurityContext
- sushto taka ne vliza v @ExpenseMethodSecurityExpressionHandler MethodSecurityExpressionOperations()
- === Creating MethodSecurityExpressionHandler Bean === === MethodSecurityConfig initialized == tva se printira koeot shte reche che beana se suzdva noqvno ne se izvika sushto taak sme antorirali @Primary MethodSecurityExpressionHandler
- opravihme problema
- i delete raobit
- sloji poel budgets v @CLAUDE.md i opishi na kude otiwa razbrabotkata do tuk
- zapishi controlerite bachakt na budgetService v @CLAUDE.md