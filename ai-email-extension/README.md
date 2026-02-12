# AI Email Reply Generator - Browser Extension

A Chrome/Firefox browser extension that generates professional email replies using AI powered by Groq.

## Installation

### For Chrome/Brave/Edge:
1. Open `chrome://extensions/` (or your browser's equivalent)
2. Enable **Developer Mode** (top right corner)
3. Click **Load unpacked**
4. Select the `ai-email-extension` folder
5. The extension should now appear in your extensions list

### For Firefox:
1. Open `about:debugging#/runtime/this-firefox`
2. Click **Load Temporary Add-on**
3. Select any file from the `ai-email-extension` folder
4. The extension should now appear in your extensions list

## Requirements

- Backend server must be running on `http://localhost:8082`
- Start backend with: `mvnw spring-boot:run` (from AI_EMAIL_REPLY directory)
- Gmail account (for Gmail integration features)

## Features

✨ **Quick Reply Generation**
- Paste email content into the extension popup
- Select tone (Professional/Friendly/Formal/Casual)
- Generate AI-powered reply in seconds

📋 **Copy to Clipboard**
- Copy generated reply with one click
- Use anywhere, not just Gmail

✉️ **Gmail Integration** (Experimental)
- Open email in Gmail
- Click "Generate Reply" button in extension
- Reply automatically extracts from email
- Insert generated reply directly into Gmail composer

## Usage

1. Click the extension icon in your browser toolbar
2. Paste the email content you want to reply to
3. Select the desired tone (optional)
4. Click "Generate Reply"
5. Copy or insert the generated reply

## Configuration

To change the backend API URL, edit `popup.js`:
```javascript
const API_URL = 'http://localhost:8082/api/email/generate';
```

## Troubleshooting

**"Disconnected ✗" Status:**
- Make sure backend is running: `mvnw spring-boot:run`
- Check if running on correct port (8082)
- Verify CORS is enabled on backend

**Gmail Integration Not Working:**
- Refresh Gmail page after installing extension
- Make sure you're on https://mail.google.com
- Check browser console for errors (F12)

**Generated Reply is Empty:**
- Check backend logs for errors
- Verify Groq API key is valid
- Try with different email content

## Files

- `manifest.json` - Extension configuration
- `popup.html` - Extension popup UI
- `popup.js` - Popup functionality and API calls
- `popup.css` - Popup styling
- `content.js` - Gmail integration script
- `background.js` - Background service worker
- `content.css` - Gmail integration styles

## Permissions

The extension requests:
- `activeTab` - Access current tab
- `scripting` - Inject scripts into Gmail
- `storage` - Store user preferences
- `https://mail.google.com/*` - Access Gmail
- `http://localhost:8082/*` - Access backend API

## Privacy

- No data is sent to external services except Groq API
- All processing happens on your local backend
- Groq API interactions are logged locally only
- No tracking or analytics

## License

MIT License - Feel free to modify and distribute

## Support

For issues or feature requests, please check:
- Backend logs for API errors
- Browser console (F12) for JavaScript errors  
- Extension status indicator shows backend connection status
