// material-ui
import { useTheme } from '@mui/material/styles';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

// project imports
import { withAlpha } from 'utils/colorUtils';
import MainCard from 'components/MainCard';
import IconButton from 'components/@extended/IconButton';

// assets
import LockOutlined from '@ant-design/icons/LockOutlined';

// ==============================|| SECURITY CARD ||============================== //

export default function SecurityCard() {
  const theme = useTheme();

  return (
    <MainCard
      sx={{
        bgcolor: withAlpha(theme.vars.palette.info.darker, 0.9),
        borderColor: 'info.dark',
        width: 200,
        position: 'relative',
        boxShadow: `0 20px 40px ${withAlpha(theme.palette.common.black, 0.3)}`,
        ...theme.applyStyles('dark', { bgcolor: withAlpha(theme.vars.palette.info.lighter, 0.9), borderColor: 'info.light' })
      }}
    >
      <Stack sx={{ gap: 1.5 }}>
        <Stack direction="row" sx={{ gap: 1, alignItems: 'center', width: 1 }}>
          <Typography variant="subtitle2" sx={{ color: 'info.main' }}>
            SECURITY
          </Typography>
          <IconButton
            size="small"
            shape="rounded"
            variant="contained"
            color="success"
            sx={{ ml: 'auto', alignSelf: 'right', bgcolor: 'success.dark' }}
          >
            <LockOutlined />
          </IconButton>
        </Stack>
        <Stack sx={{ gap: 0.5 }}>
          <Typography variant="h4" sx={{ color: 'secondary.lighter', ...theme.applyStyles('dark', { color: 'secondary.darker' }) }}>
            Sanitize Inputs
          </Typography>
          <Typography variant="caption" sx={{ color: 'info.lighter', ...theme.applyStyles('dark', { color: 'info.darker' }) }}>
            Validate all dynamic entry points automatically.
          </Typography>
        </Stack>
        <Chip label="Core" size="small" sx={{ bgcolor: withAlpha('#fff', 0.15), color: 'common.white' }} />
      </Stack>
    </MainCard>
  );
}
