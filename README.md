# Дипломная работа

--------------------

## Cloude Storage

Schema of _project_
```
cloud-storage/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── ru/netology/
│   │       │       ├── CloudStorageApplication.java
│   │       │       ├── config/
│   │       │       │   ├── SecurityConfig.java
│   │       │       │   ├── CorsConfig.java
│   │       │       │   └── JwtConfig.java
│   │       │       ├── controller/
│   │       │       │   ├── AuthController.java
│   │       │       │   └── FileController.java
│   │       │       ├── service/
│   │       │       │   ├── AuthService.java
│   │       │       │   └── FileService.java
│   │       │       ├── repository/
│   │       │       │   ├── UserRepository.java
│   │       │       │   ├── FileRepository.java
│   │       │       │   └── TokenRepository.java
│   │       │       ├── model/
│   │       │       │   ├── User.java
│   │       │       │   ├── FileInfo.java
│   │       │       │   └── Token.java
│   │       │       └── dto/
│   │       │           ├── LoginRequest.java
│   │       │           ├── JwtResponse.java
│   │       │           └── FileMetadata.java
│   │       └── resources/
│   │           ├── application.yml
│   │           └── db/
│   │               └── migration/
│   │                   └── V1__Init.sql
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
└── frontend/  (скачанный FRONT)
```
___
**Backend**

1. *CloudStorageApplication.java*
   ___
**Назначение:** Точка входа в Spring Boot приложение.
    
**Что делает:** Запускает Spring контекст, сканирует компоненты, запускает веб-сервер.


2. *SecurityConfig.java*

**Назначение:** Настройка безопасности Spring Security.

**Что делает:**
- Отключает CSRF (не нужно для REST API)
- Разрешает /login всем
- Требует авторизацию для /logout, /list, /file
- Устанавливает режим STATELESS — без сессий, только JWT

PasswordEncoder: Хэширует пароли (BCrypt)

3. *CorsConfig.java*

**Назначение:** Настройка CORS (Cross-Origin Resource Sharing).

**Что делает:** Позволяет FRONT (на localhost:8081) обращаться к BACKEND (на localhost:8080).

4. *AuthController.java*

**Назначение:** Обработка запросов на авторизацию.

**Что делает:**
- /login — принимает логин/пароль, возвращает auth-token
- /logout — принимает auth-token, удаляет его (деактивирует)

5. *FileController.java*

**Назначение:** Обработка запросов на файловое хранилище.

**Что делает:**
- /list — возвращает список файлов пользователя
- /file (POST) — загружает файл
- /file (DELETE) — удаляет файл

6. *AuthService.java*

**Назначение:** Логика аутентификации.

**Что делает:**
- login: проверяет логин/пароль, генерирует JWT, сохраняет токен в БД
- logout: находит токен в БД и устанавливает active = false

7. *FileService.java*

**Назначение:** Логика работы с файлами.

**Что делает:**
- listFiles: находит файлы пользователя по токену
- saveFile: сохраняет файл на диск, записывает метаданные в БД
- тdeleteFile: удаляет файл с диска и из БД
- getUserIdByToken: извлекает userId по активному токену

8. *UserRepository.java*

**Назначение:** Работа с сущностью User.

**Что делает:** Наследуется от JpaRepository → предоставляет CRUD-операции.
findByLogin — метод-запрос для поиска пользователя по логину.

9. *FileRepository.java*

**Назначение:** Работа с сущностью FileInfo.

findByUserId — возвращает все файлы конкретного пользователя.

10. *TokenRepository.java*

**Назначение:** Работа с сущностью Token.

findByTokenValueAndActiveTrue — ищет активный токен по значению.

11. *User.java*

**Назначение:** Модель пользователя.

**Что делает:** Сущность для таблицы users в БД.

12. *FileInfo.java*

**Назначение:** Модель файла.

**Что делает:** Хранит метаданные файла (имя, размер, путь, владелец).

13. *Token.java*

**Назначение:** Модель токена.

**Что делает:** Хранит JWT-токен, привязанный к пользователю, и его статус.

14. *LoginRequest.java*

**Назначение:** DTO для тела запроса /login.

15. *JwtResponse.java*

**Назначение:** DTO для ответа /login.

**Что делает:** Возвращает токен в поле auth-token, как требует FRONT.

16. *resources/application.yml*

**Назначение:** Конфигурация приложения.

**Что делает:** Подключение к БД, настройка JPA, Flyway, JWT.

17. *resources/db/migration/V1__Init.sql*

**Назначение:** Скрипт инициализации БД.

**Что делает:** Создаёт таблицы users, tokens, files.

18. *Dockerfile*

**Назначение:** Инструкции для сборки Docker-образа.

**Что делает:** Берёт JAR-файл и запускает его.

19. *docker-compose.yml*

**Назначение:** Оркестрация контейнеров.

**Что делает:** Запускает PostgreSQL и BACKEND, связывает их.

20. *pom.xml*

**Назначение:** Описание Maven-проекта.

**Что делает:** Указывает зависимости, плагины, версии.
