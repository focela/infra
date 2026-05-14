// material-ui
import { useTheme } from '@mui/material/styles';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// project imports
import MainCard from 'components/MainCard';
import { withAlpha } from 'utils/colorUtils';

// ==============================|| PERFORMANCE CARD ||============================== //

export default function PerformanceCard() {
  const theme = useTheme();

  return (
    <MainCard
      content={false}
      sx={{
        width: 260,
        bgcolor: withAlpha(theme.palette.secondary.darker, 0.85),
        borderColor: 'success.dark',
        p: 2,
        ...theme.applyStyles('dark', { borderColor: 'success.light' })
      }}
    >
      <Stack sx={{ gap: 1 }}>
        <Typography variant="subtitle1" sx={{ color: 'success.main' }}>
          PERFORMANCE
        </Typography>
        <Stack direction="row" spacing={1} alignItems="center" sx={{ mt: -0.5 }}>
          <Typography sx={{ color: 'warning.main', fontSize: '1rem' }}>★</Typography>
          <Box
            sx={{
              width: 22,
              height: 22,
              borderRadius: '50%',
              bgcolor: withAlpha('#fff', 0.1),
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            <Typography sx={{ color: withAlpha('#fff', 0.6), fontSize: '0.7rem' }}>🔒</Typography>
          </Box>
          <Typography sx={{ color: 'secondary.main' }}>ntis</Typography>
          <Typography sx={{ color: 'secondary.main' }}>{'();'}</Typography>
        </Stack>
        <Stack sx={{ gap: 0.25 }}>
          <Typography variant="h4" sx={{ color: 'secondary.lighter', ...theme.applyStyles('dark', { color: 'secondary.darker' }) }}>
            Clean React Comp
          </Typography>
          <Typography variant="caption" sx={{ color: 'secondary.main' }}>
            Decompose large components into hooks & pure functions.
          </Typography>
        </Stack>
      </Stack>
    </MainCard>
  );
}
