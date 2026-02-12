# AI Email Reply Generator

A modern web application that uses Google's Gemini AI to intelligently generate email replies. This full-stack application combines a Spring Boot backend with a React frontend to provide a seamless user experience for automated email composition.

## Project Overview

**Email Reply Generator** leverages artificial intelligence to analyze incoming emails and automatically generate appropriate, contextual responses. Users can customize the tone of the response to match their communication style.

### Key Features
- 🤖 AI-powered email reply generation using Google Gemini API
- 🎨 Modern Material-UI interface for intuitive user interaction
- 🔧 Reactive backend with Spring WebFlux
- ⚡ Fast, responsive frontend built with React and Vite
- 🎯 Tone customization (Professional, Friendly, Formal, etc.)
- 🔄 Real-time loading states and error handling

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.5.4
- **Language**: Java 21
- **Architecture**: Spring WebFlux (Reactive/Non-blocking)
- **Build Tool**: Maven
- **Dependencies**:
  - Spring Boot Web Starter
  - Spring Boot WebFlux (for reactive programming)
  - Lombok (for reducing boilerplate)
  - Google Gemini API integration

### Frontend
- **Framework**: React 19
- **Build Tool**: Vite
- **Package Manager**: npm
- **UI Library**: Material-UI (MUI v7)
- **HTTP Client**: Axios
- **Dependencies**:
  - Emotion (CSS-in-JS styling)
  - React DOM 19

## System Requirements

### Prerequisites
- **Java**: JDK 21 or higher
- **Node.js**: v18 or higher
- **npm**: v9 or higher
- **Maven**: v3.6 or higher (or use the included mvnw wrapper)
- **Git**: for version control
- **Google Gemini API Key**: Get one from [Google AI Studio](https://aistudio.google.com/app/apikey)

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd "ai email replyer extension"
cd AI_EMAIL_REPLY
```

### 2. Configure Backend

#### Update API Key
Edit the file `src/main/resources/application.properties`:
```properties
spring.application.name=AI_EMAIL_REPLY
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
server.port=8082
```

**⚠️ IMPORTANT**: Replace `YOUR_GEMINI_API_KEY_HERE` with your actual Gemini API key from Google AI Studio.

#### Install Java Dependencies
```bash
./mvnw clean install
```

On Windows, use:
```bash
mvnw.cmd clean install
```

### 3. Configure Frontend

Navigate to the React directory:
```bash
cd AI-REPLY_REACT
npm install
```

## Running the Application

### Option 1: Run Both Services (Recommended for Development)

#### Terminal 1 - Start Backend Server
```bash
cd AI_EMAIL_REPLY  # if not already there
./mvnw spring-boot:run
```

Or on Windows:
```bash
mvnw.cmd spring-boot:run
```

**Expected output:**
```
Started AiEmailReplyApplication in X.XXX seconds
Tomcat started on port(s): 8082
```

#### Terminal 2 - Start Frontend Development Server
```bash
cd AI-REPLY_REACT
npm run dev
```

**Expected output:**
```
VITE v7.0.4 ready in XXX ms

➜  Local:   http://localhost:5173/
```

### Option 2: Production Build

#### Build Backend
```bash
./mvnw clean package
java -jar target/AI_EMAIL_REPLY-0.0.1-SNAPSHOT.jar
```

#### Build Frontend
```bash
cd AI-REPLY_REACT
npm run build
npm run preview
```

## API Endpoints

### Generate Email Reply
- **Endpoint**: `POST /api/email/generate`
- **Base URL**: `http://localhost:8082`
- **Port**: 8082
- **CORS**: Enabled for all origins

#### Request Body
```json
{
  "emailContent": "Dear Sir/Madam, I wanted to inquire about...",
  "tone": "Professional"
}
```

#### Response
```json
{
  "generatedReply": "Thank you for your inquiry. I appreciate your interest in..."
}
```

#### Example cURL Request
```bash
curl -X POST http://localhost:8082/api/email/generate \
  -H "Content-Type: application/json" \
  -d '{
    "emailContent": "Your email content here",
    "tone": "Professional"
  }'
```

## Project Structure

```
AI_EMAIL_REPLY/
├── pom.xml                          # Maven configuration
├── mvnw / mvnw.cmd                  # Maven wrapper scripts
├── src/
│   ├── main/
│   │   ├── java/com/email/AI_EMAIL_REPLY/
│   │   │   ├── AiEmailReplyApplication.java      # Spring Boot entry point
│   │   │   ├── EmailGeneratorController.java      # REST controller
│   │   │   ├── EmailGeneratorService.java         # Business logic
│   │   │   ├── EmailRequest.java                  # Request model
│   │   │   └── WebClientConfig.java               # WebFlux config
│   │   └── resources/
│   │       └── application.properties             # Configuration
│   └── test/                        # Unit tests
├── AI-REPLY_REACT/
│   ├── package.json                 # npm dependencies
│   ├── vite.config.js               # Vite configuration
│   ├── index.html                   # HTML entry point
│   ├── eslint.config.js             # ESLint rules
│   └── src/
│       ├── main.jsx                 # React entry point
│       ├── App.jsx                  # Main React component
│       ├── App.css                  # Styles
│       └── assets/                  # Static assets
└── .git/                            # Version control
```

## Usage Guide

1. **Open the Application**
   - Visit `http://localhost:5173` in your web browser

2. **Enter Original Email**
   - Paste or type the email content you want to reply to in the "Original Email Content" field

3. **Select Tone** (Optional)
   - Choose a tone style from the dropdown (Professional, Friendly, Formal, etc.)

4. **Generate Reply**
   - Click the "Generate Reply" button
   - Wait for the AI to process (shows loading spinner)

5. **Copy Result**
   - The generated reply appears in the output section
   - Copy and use it in your email client

## Environment Variables

The backend uses the `application.properties` file for configuration. Key variables:

| Variable | Purpose | Default |
|----------|---------|---------|
| `spring.application.name` | App name | AI_EMAIL_REPLY |
| `server.port` | Backend server port | 8082 |
| `gemini.api.key` | Google Gemini API key | Required* |
| `gemini.api.url` | Gemini API endpoint | https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent |

*Must be configured before running

## Troubleshooting

### Port Already in Use
If port 8082 or 5173 is already in use:

**For Backend (8082):**
```bash
# Change in application.properties
server.port=8083
```

**For Frontend (5173):**
```bash
npm run dev -- --port 5174
```

### API Key Errors
- Verify the Gemini API key is correctly set in `application.properties`
- Visit [Google AI Studio](https://aistudio.google.com/app/apikey) to get/regenerate your key
- Ensure the key has access to the Gemini 2.0 Flash model

### CORS Issues
If you see CORS errors:
- The backend has `@CrossOrigin(origins = "*")` enabled
- Ensure backend is running on port 8082
- Clear browser cache and restart dev server

### Build Issues
```bash
# Clean and rebuild
./mvnw clean install
cd AI-REPLY_REACT
rm -rf node_modules package-lock.json
npm install
```

## Development Tips

### Debug Backend
Set debug logs in `application.properties`:
```properties
logging.level.com.email.AI_EMAIL_REPLY=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Live Reload
- Backend: Spring Boot DevTools enabled automatically
- Frontend: Vite provides hot module replacement (HMR)

### Run Tests
```bash
./mvnw test
```

## Performance Optimization

- **Frontend**: 
  - Vite provides fast bundling and HMR
  - Material-UI components are tree-shakeable
  
- **Backend**:
  - Spring WebFlux provides non-blocking I/O
  - Reactive streams for efficient resource usage

## Security Notes

⚠️ **IMPORTANT**: 
- Never commit your API key to version control
- Use environment variables or `.env` files in production
- Consider restricting CORS origins in production (`@CrossOrigin(origins = "yourdomain.com")`)
- Keep dependencies updated for security patches

## Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -am 'Add feature'`
3. Push to branch: `git push origin feature/your-feature`
4. Submit a pull request

## License

This project is provided as-is for educational and commercial use.

## Support & Documentation

- **Spring Boot**: https://spring.io/projects/spring-boot
- **React**: https://react.dev
- **Google Gemini API**: https://ai.google.dev/docs
- **Material-UI**: https://mui.com/material-ui/
- **Vite**: https://vitejs.dev

## Next Steps

- Add user authentication
- Implement email template saving
- Add batch email processing
- Integrate with email clients (Gmail, Outlook)
- Add sentiment analysis
- Implement email storage in database
- Add user analytics

---

**Happy Coding! 🚀**

For questions or issues, please open an issue on the repository.
