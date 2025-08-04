import { useState } from 'react';
import axios from 'axios';

import {
  Box,
  Button,
  CircularProgress,
  Container,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography,
} from '@mui/material';

function App() {
  const [emailContent, setEmailContent] = useState('');
  const [tone, setTone] = useState('');
  const [generatedReply, setGeneratedReply] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async () => {
    setLoading(true);
    setError('');
   
    try {
       const response=await axios.post("http://localhost:8082/api/email/generate",{
        emailContent,
        tone
       });
       setGeneratedReply(typeof response.data==='string'? response.data:JSON.stringify(response.data));
    }catch(error){
      setError("failed to generate email reply. Please try again");
      console.error(error);

    }finally{
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        width: '100vw',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        bgcolor: '#fff', // optional: set your desired background
      }}
    >
      <Container maxWidth="md">
        <Typography variant="h3" component="h1" align="center" gutterBottom>
          Email Reply Generator
        </Typography>
        <Box sx={{ mx: 3, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            label="Original Email Content"
            fullWidth
            multiline
            rows={6}
            variant="outlined"
            value={emailContent}
            onChange={(e) => setEmailContent(e.target.value)}
            sx={{ mb: 2 }}
          />

          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel>Tone (Optional)</InputLabel>
            <Select
              value={tone}
              label="Tone (Optional)"
              onChange={(e) => setTone(e.target.value)}
            >
              <MenuItem value="">None</MenuItem>
              <MenuItem value="professional">Professional</MenuItem>
              <MenuItem value="casual">Casual</MenuItem>
              <MenuItem value="friendly">Friendly</MenuItem>
            </Select>
          </FormControl>

          <Button
            variant="contained"
            onClick={handleSubmit}
            disabled={!emailContent.trim() || loading}
            fullWidth
            sx={{ mb: 2 }}
          >
            {loading ? <CircularProgress size={24} /> : 'Generate Reply'}
          </Button>

          {error && (
            <Typography color="error" sx={{ mt: 2 }}>
              {error}
            </Typography>
          )}

          {generatedReply && (
            <Box sx={{ mt: 3, p: 2, bgcolor: "#f3f3f3", borderRadius: 2 }}>
              <Typography variant="subtitle1" gutterBottom>
                Generated Reply:
              </Typography>
              <Typography>{generatedReply}</Typography>
            </Box>
          )}
        </Box>
        {error &&(
          <Typography color='error' sx={{mb:2}}>
       {error}
        </Typography>
        )}
        {generatedReply && (
          <Box sx={{mt:3}}>
            <Typography variant='h6' gutterBottom>
          Generated Reply  
            </Typography>
            <TextField
            fullWidth
            multiline
            rows={6}
            variant='outlined'
            value={generatedReply || ''}
            InputProps={{readOnly:true}}>

            </TextField>
            <Button
            variant='outlined'
            sx={{mt:2}}
            onClick={()=>navigator.clipboard.writeText(generatedReply)}>
              Copy to Clipboard
            </Button>
          </Box>
        )}
      </Container>
    </Box>
  );
}

export default App;
