# Learning Resources & Architecture Guide

This document provides learning resources and detailed architecture explanations for understanding the AI Email Reply Generator project.

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Backend Learning Path](#backend-learning-path)
3. [Frontend Learning Path](#frontend-learning-path)
4. [API Integration Guide](#api-integration-guide)
5. [Key Concepts & Design Patterns](#key-concepts--design-patterns)
6. [Debugging & Troubleshooting](#debugging--troubleshooting)

---

## Architecture Overview

### System Design Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    USER BROWSER (Frontend)                   │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  React 19 App (Vite)                                │   │
│  │  ├─ App.jsx (Main Component)                        │   │
│  │  ├─ Material-UI Components (TextField, Button, etc) │   │
│  │  ├─ Axios (HTTP Client)                            │   │
│  │  └─ State Management (useState)                    │   │
│  └──────────────────────────────────────────────────────┘   │
│              │                                                │
│              │ HTTP POST /api/email/generate                  │
│              │ localhost:8082                                │
│              ▼                                                │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│              Spring Boot Backend (Port 8082)                 │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  EmailGeneratorController                           │   │
│  │  @RestController /api/email                        │   │
│  │  - @PostMapping /generate                          │   │
│  │  - Receives EmailRequest (email, tone)             │   │
│  └──────────────────────────────────────────────────────┘   │
│              │                                                │
│              │ Calls service.generateEmailReply()            │
│              ▼                                                │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  EmailGeneratorService (Business Logic)             │   │
│  │  - Processes email content & tone                   │   │
│  │  - Constructs API prompt                            │   │
│  │  - Calls Gemini API via WebClient                   │   │
│  │  - Parses response                                  │   │
│  └──────────────────────────────────────────────────────┘   │
│              │                                                │
│              │ Uses reactive WebClient                       │
│              ▼                                                │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  WebClientConfig (Spring WebFlux)                   │   │
│  │  - Configures non-blocking HTTP client              │   │
│  │  - Handles async requests/responses                 │   │
│  └──────────────────────────────────────────────────────┘   │
│              │                                                │
│              │ (Non-blocking I/O via Reactor)                │
│              ▼                                                │
└─────────────────────────────────────────────────────────────┘
                 │
                 │ HTTPS Request
                 │ API Key: AIzaSyCnHClBT2MEhhX6EW63bnFfBAEoZwgm1ms
                 ▼
      ┌──────────────────────────┐
      │  Google Gemini 2.0 API   │
      │  generativelanguage.     │
      │  googleapis.com          │
      │                          │
      │  - Analyzes email        │
      │  - Generates reply       │
      │  - Applies tone          │
      └──────────────────────────┘
```

### Data Flow
1. User inputs email content and selects tone in React UI
2. Frontend sends HTTP POST request with JSON payload to `/api/email/generate`
3. Backend controller receives request and validates input
4. Service layer constructs a prompt for Gemini AI
5. WebClient makes non-blocking HTTP call to Google's API
6. Gemini processes the input and returns generated email reply
7. Service parses and returns the result
8. Controller sends response back to frontend
9. Frontend displays the generated email reply

---

## Backend Learning Path

### Java & Spring Boot Fundamentals

#### 1. **Java 21 Features**
- **Records** (simplified data classes)
- **Text Blocks** (multi-line strings)
- **Pattern Matching** (enhanced instanceof)
- **Virtual Threads** (lightweight threading)

**Resources:**
- [Java 21 Release Notes](https://www.oracle.com/java/technologies/java-21-features.html)
- [Java Language Updates](https://docs.oracle.com/javase/21/docs/api/)

#### 2. **Spring Boot 3.5.4**
Spring Boot is an opinionated framework that simplifies Spring application development.

**Key Components Used:**
```java
@SpringBootApplication  // Main entry point
@RestController        // REST API handler
@Service              // Business logic (@Service)
@Configuration        // Config beans
@Bean                 // Component registration
```

**Resources:**
- [Spring Boot Official Guide](https://spring.io/projects/spring-boot)
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/3.5.4/reference/html/)
- [Spring Web Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)

### Spring WebFlux & Reactive Programming

#### Understanding Spring WebFlux

**What is WebFlux?**
- Non-blocking, reactive web framework
- Built on Project Reactor (reactive streams library)
- Efficient for high-concurrency scenarios
- Handles many requests with fewer threads

**Key Classes:**
- `WebClient` - Non-blocking HTTP client
- `Mono<T>` - Represents 0 or 1 value
- `Flux<T>` - Represents 0 to N values
- `Reactor` - Implements reactive streams specification

**Code Example (from the project):**
```java
// WebClientConfig handles WebClient creation
// WebClient is used in EmailGeneratorService for non-blocking API calls
webClient.post()
    .uri(url)
    .bodyValue(requestBody)
    .retrieve()
    .bodyToMono(String.class)  // Returns Mono<String> - async result
    .block()                    // Blocks for synchronous result (in this case)
```

**Resources:**
- [Spring WebFlux Guide](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Project Reactor Documentation](https://projectreactor.io/docs)
- [Reactive Programming with Spring](https://spring.io/blog/2016/06/07/notes-on-reactive-programming-part-i-the-reactive-landscape)
- [Mono and Flux Operators](https://projectreactor.io/docs/core/release/reference/)

### REST API Design

#### RESTful Endpoints
- **Stateless**: Each request contains all information
- **Resource-based**: URL represents resources (nouns)
- **HTTP Methods**: GET (retrieve), POST (create), PUT (update), DELETE (remove)

**In This Project:**
```
POST /api/email/generate
├── Resource: /email (email functionality)
├── Action: /generate (generate a reply)
├── Method: POST (creation of new content)
└── Request Body: { emailContent, tone }
```

**Resources:**
- [RESTful API Best Practices](https://restfulapi.net/)
- [HTTP Methods Guide](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods)

### Lombok & Data Classes

#### What is Lombok?
Annotation processor that generates boilerplate code:
- `@Data` - equals, hashCode, toString, getters/setters
- `@AllArgsConstructor` - constructor with all fields
- `@NoArgsConstructor` - no-arg constructor
- `@Getter` / `@Setter` - individual getters/setters

**In This Project:**
```java
@Data
public class EmailRequest {
    private String emailContent;
    private String tone;
}
// Lombok generates: getters, setters, equals, hashCode, toString
```

**Resources:**
- [Lombok Official Docs](https://projectlombok.org/)
- [Lombok Annotations Guide](https://projectlombok.org/features/all)

### Google Gemini API Integration

#### API Overview
- **Base URL**: `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent`
- **Model**: Gemini 2.0 Flash (fast, efficient)
- **Auth**: API key in request
- **Format**: JSON request/response

#### API Request Format
```json
{
  "contents": [
    {
      "parts": [
        {
          "text": "Your prompt here"
        }
      ]
    }
  ]
}
```

#### API Response Format
```json
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "Generated response text here"
          }
        ],
        "role": "model"
      }
    }
  ]
}
```

**How It's Used in This Project:**
1. Service constructs a prompt from email content and tone
2. Service sends it via WebClient to Gemini API
3. API returns generated email reply
4. Service parses the response and extracts the text
5. Controller returns it to frontend

**Resources:**
- [Google AI Studio](https://aistudio.google.com/)
- [Gemini API Documentation](https://ai.google.dev/docs)
- [Get Started with Gemini API](https://ai.google.dev/docs/gemini_api_quickstart)

#### Handling API Errors
```
Common Issues:
1. Invalid API Key
2. Rate limiting (quota exceeded)
3. Network timeouts
4. Malformed requests
5. Model not found

Solution: Implement try-catch blocks and error logging
```

---

## Frontend Learning Path

### React 19 Fundamentals

#### Key Concepts

**1. Components**
Reusable UI elements that manage their own state and lifecycle.
```jsx
function App() {
  // Component logic
  return <JSX />;
}
```

**2. Hooks**
Functions that let you use state and other React features:
- **useState** - Manage component state
- **useEffect** - Handle side effects
- **useContext** - Share data across components

**In This Project:**
```jsx
const [emailContent, setEmailContent] = useState('');     // Track email input
const [tone, setTone] = useState('');                     // Track selected tone
const [generatedReply, setGeneratedReply] = useState(''); // Track output
const [loading, setLoading] = useState(false);            // Track API call state
const [error, setError] = useState('');                   // Track error messages
```

**Resources:**
- [React Official Documentation](https://react.dev)
- [React Hooks Guide](https://react.dev/reference/react/hooks)
- [useState Hook Details](https://react.dev/reference/react/useState)

**3. JSX** (JavaScript XML)
HTML-like syntax within JavaScript. Compiles to function calls.
```jsx
// JSX
<TextField label="Email" value={emailContent} />

// Compiles to:
React.createElement(TextField, { label: "Email", value: emailContent })
```

**4. Event Handling**
Responding to user interactions:
```jsx
<TextField onChange={(e) => setEmailContent(e.target.value)} />
// onChange fires when user types, extracting value from event
```

**Resources:**
- [React Events](https://react.dev/reference/react-dom/components/common#react-event-object)
- [Responding to Events](https://react.dev/learn/responding-to-events)

### Material-UI (MUI) Component Library

#### What is Material-UI?
A collection of pre-built, accessible React components following Material Design principles.

**Components Used in This Project:**
```jsx
<Box>           // Generic container (like div but styled)
<Container>     // Max-width container with padding
<TextField>     // Text input field
<Button>        // Clickable button
<Typography>    // Text with different styles
<Select>        // Dropdown menu
<CircularProgress>  // Loading spinner
<FormControl>   // Groups form elements
<InputLabel>    // Labels for inputs
<MenuItem>      // Dropdown options
```

**Example from Project:**
```jsx
<TextField
  label="Original Email Content"
  fullWidth              // Takes full width
  multiline              // Multiple lines
  rows={6}               // 6 rows tall
  variant="outlined"     // Outlined style
  value={emailContent}   // Current value
  onChange={(e) => setEmailContent(e.target.value)}  // Update on change
/>
```

**Styling with MUI:**
```jsx
<Box sx={{
  minHeight: '100vh',    // Full viewport height
  backgroundColor: '#fff',
  display: 'flex',       // CSS Flexbox
  flexDirection: 'column',
  gap: 2                 // Spacing between items
}}>
```

**Resources:**
- [Material-UI Documentation](https://mui.com/material-ui/)
- [MUI Components](https://mui.com/material-ui/api/all-packages/)
- [SX Prop Guide](https://mui.com/system/the-sx-prop/)
- [Material Design Principles](https://material.io/design)

### HTTP Requests with Axios

#### Understanding Axios
Promise-based HTTP client for making requests from the browser.

**Advantages:**
- Simpler than Fetch API
- Automatic JSON serialization
- Request/response interceptors
- Timeout support
- Cancel token support

**In This Project:**
```jsx
const response = await axios.post(
  "http://localhost:8082/api/email/generate",  // URL
  { emailContent, tone }                        // Request body
);

// Equivalent to:
// POST http://localhost:8082/api/email/generate
// Body: { "emailContent": "...", "tone": "Professional" }
```

**Error Handling:**
```jsx
try {
  const response = await axios.post(...);
  // Handle success
} catch (error) {
  setError("failed to generate email reply. Please try again");
  console.error(error);  // Log for debugging
}
```

**Resources:**
- [Axios Documentation](https://axios-http.com/docs/intro)
- [HTTP Methods with Axios](https://axios-http.com/docs/api_intro)
- [Request Configuration](https://axios-http.com/docs/req_config)
- [Handling Errors](https://axios-http.com/docs/handling_errors)

### Vite Build Tool

#### What is Vite?
Modern frontend build tool that:
- Provides lightning-fast HMR (Hot Module Replacement)
- Optimizes assets for production
- Uses ES modules during development
- Minimal configuration

**Development Server:**
```bash
npm run dev
# Starts dev server on localhost:5173
# Watches for file changes and reloads instantly
```

**Production Build:**
```bash
npm run build
# Creates optimized dist/ folder
# Ready for deployment
```

**Resources:**
- [Vite Official Site](https://vitejs.dev)
- [Vite Configuration](https://vitejs.dev/config/)
- [Vite React Plugin](https://github.com/vitejs/vite-plugin-react)

### Emotion CSS-in-JS

#### What is Emotion?
CSS-in-JS library used by MUI for styling.

**Benefits:**
- Write CSS in JavaScript
- Scoped styles (no global conflicts)
- Dynamic styling based on state
- Type-safe (with TypeScript)

**Already integrated through MUI's `sx` prop:**
```jsx
<Box sx={{
  color: 'red',           // CSS properties
  backgroundColor: 'blue',
  '&:hover': {           // Pseudo-selectors
    backgroundColor: 'darkblue'
  },
  '@media (max-width: 600px)': {  // Media queries
    fontSize: '12px'
  }
}}>
```

**Resources:**
- [Emotion Documentation](https://emotion.sh/docs/introduction)
- [MUI SX Prop](https://mui.com/system/the-sx-prop/)

---

## API Integration Guide

### Testing the API Manually

#### Using cURL (Command Line)
```bash
curl -X POST http://localhost:8082/api/email/generate \
  -H "Content-Type: application/json" \
  -d '{
    "emailContent": "Hi, I received your proposal. Could you provide more details?",
    "tone": "Professional"
  }'
```

#### Using Postman (GUI Tool)
1. Open Postman
2. Create new POST request
3. URL: `http://localhost:8082/api/email/generate`
4. Headers: `Content-Type: application/json`
5. Body (raw JSON):
```json
{
  "emailContent": "Thank you for your interest...",
  "tone": "Friendly"
}
```
6. Click "Send"

#### Using VS Code REST Client Extension
Create a file `test.http`:
```http
POST http://localhost:8082/api/email/generate
Content-Type: application/json

{
  "emailContent": "Your email here",
  "tone": "Professional"
}
```

**Resources:**
- [Postman Learning Center](https://learning.postman.com/)
- [cURL Documentation](https://curl.se/)
- [VS Code REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client)

---

## Key Concepts & Design Patterns

### 1. Model-View-Controller (MVC)

#### Backend (Spring MVC)
```
Model (EmailRequest) → Controller (EmailGeneratorController) → Service (EmailGeneratorService) → View (JSON Response)
```

**Model:** `EmailRequest.java` - Data structure
**View:** JSON Response - Returned to client
**Controller:** Handles HTTP requests and coordinates with service

**Resources:**
- [Spring MVC Tutorial](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc)

### 2. Dependency Injection (DI)

#### Spring's Dependency Injection
Instead of manually creating objects, Spring injects them:

```java
@RestController
@AllArgsConstructor  // Lombok generates constructor
public class EmailGeneratorController {
    private final EmailGeneratorService emailGeneratorService;
    // emailGeneratorService is injected by Spring
}
```

**Benefits:**
- Loose coupling between classes
- Easier testing (mock dependencies)
- Centralized configuration
- Cleaner code

**Resources:**
- [Spring DI Guide](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans)
- [Inversion of Control](https://www.baeldung.com/inversion-of-control)

### 3. Reactive Programming

#### Synchronous vs Asynchronous

**Traditional Synchronous:**
```
Thread 1: Request → API Call (waits) → Response
Thread 2: Request → API Call (waits) → Response
Thread 3: ...
```
Each request blocks a thread (expensive on resources)

**Reactive/Asynchronous:**
```
Thread: Request1 → API Call (non-blocking)
        Request2 → API Call (non-blocking)
        ...
        (When Response arrives, thread resolves promise)
```
One thread handles many requests efficiently

**In This Project:**
- `WebClient` is reactive (doesn't block)
- Can handle thousands of concurrent requests
- More efficient than traditional HttpClient

**Resources:**
- [Reactive Programming Guide](https://spring.io/projects/reactor)
- [Understanding Reactive Streams](https://www.baeldung.com/reactive-programming)

### 4. CORS (Cross-Origin Resource Sharing)

#### Why is CORS needed?
Browsers prevent JavaScript from making requests to different domains for security.

**In This Project:**
```java
@CrossOrigin(origins = "*")  // Allow all origins
```

**In Production:**
```java
@CrossOrigin(origins = "https://yourdomain.com")  // Specific domain
```

**Resources:**
- [MDN: CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Spring CORS Guide](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-cors)

---

## Debugging & Troubleshooting

### Backend Debugging

#### 1. Enable Debug Logging
Edit `src/main/resources/application.properties`:
```properties
logging.level.root=INFO
logging.level.com.email.AI_EMAIL_REPLY=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### 2. Read Logs
Look for:
```
Successfully called Gemini API
Error calling Gemini API
Parsing response...
```

#### 3. Check API Response
Add logging in `EmailGeneratorService`:
```java
System.out.println("Response from Gemini: " + response);
```

#### 4. Debug in IDE
- Set breakpoints in IntelliJ IDEA
- Run with debugger: click Debug icon
- Step through code with F10 (step over) or F11 (step into)

### Frontend Debugging

#### 1. Browser DevTools
Press `F12` in Chrome/Firefox to open Developer Tools
- **Console tab**: See JavaScript errors and console.log() output
- **Network tab**: See HTTP requests/responses
- **React Developer Tools extension**: Inspect React components

#### 2. Check Network Requests
1. Open DevTools → Network tab
2. Click "Generate Reply" button
3. Look for POST request to `/api/email/generate`
4. Click on it to see:
   - Request headers
   - Request body (JSON sent)
   - Response (JSON received)
   - Status code

#### 3. Common Frontend Errors
- **"Cannot POST /api/email/generate"** → Backend not running
- **"CORS error"** → Backend CORS not configured
- **"Failed to load localhost:8082"** → Backend port wrong

#### 4. Frontend Logging
Already in code:
```jsx
catch(error){
  console.error(error);  // Logs error to console
  setError("failed to generate email reply");
}
```

**Resources:**
- [Chrome DevTools Guide](https://developer.chrome.com/docs/devtools/)
- [Firefox Developer Tools](https://developer.mozilla.org/en-US/docs/Tools)
- [React DevTools Extension](https://react.dev/learn/react-developer-tools)

### Common Issues & Solutions

#### Issue: "API Key Invalid"
**Solution:**
1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Create new key
3. Update in `application.properties`
4. Restart backend

#### Issue: "localhost:8082 refused connection"
**Solution:**
- Backend not running
- Run: `./mvnw spring-boot:run`
- Wait for startup message

#### Issue: "Failed to generate email reply"
**Solution:**
1. Check browser console (F12)
2. Check backend logs (terminal)
3. Verify API key
4. Test API with cURL manually

#### Issue: "Timeout waiting for response"
**Solution:**
- Gemini API may be slow
- Increase timeout in WebClientConfig
- Check internet connection

---

## Advanced Topics

### Testing your Endpoints

#### JUnit 5 & Mockito
```java
@SpringBootTest
class EmailGeneratorControllerTest {
    @MockBean
    private EmailGeneratorService service;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testGenerateEmail() throws Exception {
        // Arrange
        when(service.generateEmailReply(any()))
            .thenReturn("Generated reply");
        
        // Act & Assert
        mockMvc.perform(post("/api/email/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"emailContent\":\"test\",\"tone\":\"Professional\"}"))
            .andExpect(status().isOk());
    }
}
```

**Resources:**
- [Spring Testing Guide](https://spring.io/guides/gs/testing-web/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)

### Performance Optimization

**Backend:**
- Use caching for repeated requests
- Implement rate limiting
- Use database instead of API for historical data
- Add connection pooling

**Frontend:**
- Lazy load components
- Memoize expensive computations (useMemo)
- Code splitting with dynamic imports
- Compress images

### Security Considerations

1. **Never commit API keys** → Use environment variables
2. **Validate input** → Prevent injection attacks
3. **Use HTTPS** → Encrypt data in transit
4. **Implement authentication** → Verify user identity
5. **Rate limiting** → Prevent abuse
6. **CORS properly** → Don't allow all origins

---

## Learning Resources Summary

### Must-Read
1. [Spring Boot Getting Started Guide](https://spring.io/guides/gs/spring-boot/)
2. [React Official Tutorial](https://react.dev/learn)
3. [Google Gemini API Docs](https://ai.google.dev/docs)
4. [Material-UI Introduction](https://mui.com/material-ui/getting-started/)

### Practice
1. Modify the tone options
2. Add new API endpoints
3. Implement user authentication
4. Add email history storage
5. Create different email templates

### Videos
- [Spring Boot Full Course (Coursera)](https://www.coursera.org/learn/spring-boot)
- [React Tutorial Series (Official)](https://react.dev/learn/tutorial-tic-tac-toe)
- [Material-UI Tutorial (YouTube)](https://www.youtube.com/results?search_query=material+ui+tutorial)

---

**Keep learning and exploring! 🚀**

This project is a great foundation to understand full-stack web development with modern technologies.

