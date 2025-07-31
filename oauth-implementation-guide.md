# OAuth2 Authentication Implementation Guide - Стъпка по стъпка

## Обзор
Тази имплементация добавя OAuth2 автентификация към Spring Boot приложение, позволявайки на потребителите да се логват чрез GitHub (или други OAuth2 провайдери).

## Стъпка 1: Добавяне на Dependencies

В `build.gradle` добавете OAuth2 dependency:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    // други dependencies...
}
```

## Стъпка 2: Конфигуриране на OAuth2 в application.yaml

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: ${GITHUB_CLIENT:}
            client-secret: ${GITHUB_SECRET:}
            scope: user:email
          # Можете да добавите и други провайдери:
          # google:
          #   client-id: ${GOOGLE_CLIENT:}
          #   client-secret: ${GOOGLE_SECRET:}
          #   scope: profile,email
```

## Стъпка 3: Създаване на OAuth Success Handler

### OAuthSuccessHandler.java
```java
@Component
public class OAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    public OAuthSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws ServletException, IOException {

        if (authentication instanceof OAuth2AuthenticationToken token) {
            OAuth2User user = token.getPrincipal();

            // Извличане на данни от OAuth2 провайдера
            String email = user.getAttribute("email");
            String name = user.getAttribute("name");

            // Създаване на потребител ако не съществува
            userService.createUserIfNotExist(email, name);
            
            // Логване на потребителя в Spring Security контекста
            authentication = userService.login(email);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
```

## Стъпка 4: Конфигуриране на Security Configuration

В `SecurityConfiguration.java` добавете OAuth2 конфигурация:

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, 
                                         OAuthSuccessHandler oAuthSuccessHandler) throws Exception {
        return httpSecurity
            .authorizeHttpRequests(
                authorizeRequests -> authorizeRequests
                    // публични страници
                    .requestMatchers("/", "/users/login", "/users/register").permitAll()
                    // всички други изискват автентификация
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin -> {
                formLogin
                    .loginPage("/users/login")
                    .defaultSuccessUrl("/")
                    .failureForwardUrl("/users/login-error");
            })
            .oauth2Login(
                oauth -> oauth.successHandler(oAuthSuccessHandler)  // Добавяне на custom success handler
            )
            .build();
    }
}
```

## Стъпка 5: Разширяване на UserService Interface

```java
public interface UserService {
    void registerUser(UserRegistrationDTO userRegistrationDTO);
    
    // Методи за OAuth2
    void createUserIfNotExist(String email, String names);
    Authentication login(String email);
}
```

## Стъпка 6: Имплементация на OAuth2 методите в UserService

### UserServiceImpl.java
```java
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MobileleUserDetailsService mobileleUserDetailsService;

    @Override
    public void createUserIfNotExist(String email, String names) {
        // Проверка дали потребителят вече съществува
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isEmpty()) {
            // Разделяне на имената
            String[] nameParts = names.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            // Създаване на нов потребител
            UserEntity newUser = new UserEntity()
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setActive(true)
                .setPassword(passwordEncoder.encode("oauth2user")); // Dummy password за OAuth2 потребители

            // Добавяне на роля USER по подразбиране
            UserRoleEntity userRole = userRoleRepository.findByRole(UserRoleEnum.USER);
            newUser.setRoles(List.of(userRole));

            userRepository.save(newUser);
        }
    }

    @Override
    public Authentication login(String email) {
        UserDetails userDetails = mobileleUserDetailsService.loadUserByUsername(email);
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
            userDetails,
            userDetails.getPassword(),
            userDetails.getAuthorities()
        );
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        return auth;
    }
}
```

## Стъпка 7: Настройка на Environment Variables

Създайте environment variables или добавете в application.yaml:

```bash
# Environment Variables
GITHUB_CLIENT=your_github_client_id
GITHUB_SECRET=your_github_client_secret
```

## Стъпка 8: HTML Template за Login с OAuth2

### auth-login.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Login</title>
</head>
<body>
    <!-- Обикновен form login -->
    <form th:action="@{/users/login}" method="post">
        <input type="email" name="email" placeholder="Email" required>
        <input type="password" name="password" placeholder="Password" required>
        <button type="submit">Login</button>
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
    </form>

    <!-- OAuth2 Login бутони -->
    <div>
        <h3>Or login with:</h3>
        <a th:href="@{/oauth2/authorization/github}">
            <button type="button">Login with GitHub</button>
        </a>
        <!-- За Google OAuth2 -->
        <!-- <a th:href="@{/oauth2/authorization/google}">
            <button type="button">Login with Google</button>
        </a> -->
    </div>
</body>
</html>
```

## Стъпка 9: Конфигуриране на GitHub OAuth Application

1. Отидете на GitHub > Settings > Developer settings > OAuth Apps
2. Създайте нова OAuth App с:
   - Application name: Вашето приложение
   - Homepage URL: `http://localhost:8080`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
3. Копирайте Client ID и Client Secret

## Стъпка 10: Тестване на имплементацията

1. Стартирайте приложението
2. Отидете на `/users/login`
3. Кликнете на "Login with GitHub"
4. Разрешете достъп на приложението
5. Потребителят се автоматично създава и логва

## Ключови особености:

### Security Flow:
1. Потребителят избира OAuth2 login
2. Пренасочва се към провайдера (GitHub)
3. След успешна автентификация се връща на `/login/oauth2/code/github`
4. `OAuthSuccessHandler` се извиква автоматично
5. Извличат се данни от OAuth2User
6. Създава се потребител ако не съществува
7. Потребителят се логва в Spring Security контекста

### Важни endpoints:
- `/oauth2/authorization/github` - започва OAuth2 flow
- `/login/oauth2/code/github` - callback URL след autorizация
- `/users/login` - обикновен form login

### Конфигурация за други провайдери:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: profile,email
          facebook:
            client-id: ${FACEBOOK_CLIENT_ID}
            client-secret: ${FACEBOOK_CLIENT_SECRET}
            scope: email,public_profile
```

Тази имплементация осигурява пълна интеграция на OAuth2 автентификация със съществуващата Spring Security конфигурация.