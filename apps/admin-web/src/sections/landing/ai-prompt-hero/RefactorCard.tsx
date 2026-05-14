// material-ui
import { useTheme } from '@mui/material/styles';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

// project imports
import { withAlpha } from 'utils/colorUtils';
import IconButton from 'components/@extended/IconButton';
import MainCard from 'components/MainCard';

// assets
import PlusOutlined from '@ant-design/icons/PlusOutlined';
import RightOutlined from '@ant-design/icons/RightOutlined';

// ==============================|| REFACTOR CARD ||============================== //

export default function RefactorCard() {
  const theme = useTheme();

  return (
    <MainCard
      content={false}
      sx={{
        p: 2,
        bgcolor: withAlpha(theme.palette.secondary.darker, 0.85),
        borderColor: 'primary.darker',
        width: 240,
        position: 'relative',
        ...theme.applyStyles('dark', { borderColor: 'primary.light' })
      }}
    >
      <Stack sx={{ gap: 1.5 }}>
        <Stack direction="row" sx={{ gap: 1, alignItems: 'center', width: 1 }}>
          <Typography variant="subtitle2" sx={{ color: 'primary.light', ...theme.applyStyles('dark', { color: 'primary.dark' }) }}>
            REFACTOR
          </Typography>
          <Chip label="New" size="small" color="error" />
          <IconButton size="small" shape="rounded" variant="contained" sx={{ ml: 'auto', alignSelf: 'right', bgcolor: 'primary.dark' }}>
            <RightOutlined />
          </IconButton>
        </Stack>
        <Stack sx={{ gap: 0.5 }}>
          <Typography variant="h4" sx={{ color: 'secondary.lighter', ...theme.applyStyles('dark', { color: 'secondary.darker' }) }}>
            Optimize API Call...
          </Typography>
          <Typography variant="caption" sx={{ color: 'primary.lighter', ...theme.applyStyles('dark', { color: 'primary.darker' }) }}>
            Reduce latency by batching concurrent requests.
          </Typography>
        </Stack>
        <Stack direction="row" alignItems="center" justifyContent="space-between">
          <Chip label="PRO" size="small" color="primary" />
          <IconButton size="small" shape="rounded" variant="contained" color="success" sx={{ bgcolor: 'success.dark' }}>
            <PlusOutlined />
          </IconButton>
        </Stack>
      </Stack>
    </MainCard>
  );
}
