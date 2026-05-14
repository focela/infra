// material-ui
import { useColorScheme, useTheme } from '@mui/material/styles';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// project imports
import MainCard from 'components/MainCard';
import { ThemeMode } from 'config';
import { withAlpha } from 'utils/colorUtils';
import SyntaxHighlight from 'utils/SyntaxHighlight';

const codeString = `# Mantis React TypeScript Admin Template - Complete AI Instructions

## Role & Context
You are a 'Frontend Template Architect'. Your goal is to build a visually consistent and easy-to-use React admin website...

## Project Overview
 - Mantis is a Material-UI-based admin dashboard template with two variants:
  1. full-version (/full-version): All features, components, and integrations
  2. seed (/seed): Minimal scaffold for custom development
  ...

## Tech Stack
- **Framework**: React (Functional Components)
`;

// ==============================|| CODE EDITOR CARD ||============================== //

export default function CodeEditorCard() {
  const theme = useTheme();
  const { colorScheme } = useColorScheme();

  return (
    <MainCard
      content={false}
      sx={{
        p: 2,
        bgcolor: withAlpha('grey.700', 0.98),
        borderColor: 'grey.600',
        width: { xs: 480, lg: 680 },
        backdropFilter: 'blur(20px)',
        boxShadow: `0 25px 50px ${withAlpha(theme.palette.common.black, 0.4)}`,
        '&& pre': { backgroundColor: `${theme.vars.palette.grey[colorScheme === ThemeMode.DARK ? 100 : 700]} !important` },
        ...theme.applyStyles('dark', { bgcolor: withAlpha('grey.100', 0.98), borderColor: 'grey.200' })
      }}
    >
      {/* window controls */}
      <Stack direction="row" sx={{ gap: 0.75, alignItems: 'center', mb: 2 }}>
        <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'error.main' }} />
        <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'warning.main' }} />
        <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'success.main' }} />
        <Typography variant="caption" sx={{ color: 'text.secondary', pl: 1.5, mt: 0.5 }}>
          AGENTS.md
        </Typography>
      </Stack>

      {/* Code content */}
      <SyntaxHighlight language="markdown" wrapLongLines={true} darkStyle>
        {codeString}
      </SyntaxHighlight>
    </MainCard>
  );
}
