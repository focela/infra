// material-ui
import { useTheme } from '@mui/material/styles';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

// project imports
import { withAlpha } from 'utils/colorUtils';
import MainCard from 'components/MainCard';

// ==============================|| REFACTORING TOOLTIP ||============================== //

export default function RefactoringTooltip() {
  const theme = useTheme();

  return (
    <MainCard sx={{ width: 260, bgcolor: withAlpha(theme.palette.info.darker, 0.85), borderColor: 'info.dark' }}>
      <Stack direction="row" sx={{ gap: 1, alignItems: 'center' }}>
        <Typography variant="subtitle1">✨</Typography>
        <Typography variant="subtitle1" sx={{ color: 'info.lighter', ...theme.applyStyles('dark', { color: 'info.dark' }) }}>
          Refactoring logic...
        </Typography>
      </Stack>
    </MainCard>
  );
}
