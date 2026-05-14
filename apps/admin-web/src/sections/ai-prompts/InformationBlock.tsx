// material-ui
import { useTheme } from '@mui/material/styles';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

// third-party
import { motion } from 'framer-motion';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import Avatar from 'components/@extended/Avatar';
import getColors from 'utils/getColors';
import { withAlpha } from 'utils/colorUtils';

// types
import { ColorProps } from 'types/extended';

// assets
import CopyOutlined from '@ant-design/icons/CopyOutlined';
import SafetyCertificateOutlined from '@ant-design/icons/SafetyCertificateOutlined';
import SlidersOutlined from '@ant-design/icons/SlidersOutlined';

interface FeatureItemProps {
  icon: React.ReactNode;
  title: string;
  description: string;
  delay: number;
  iconColor?: ColorProps;
}

// ==============================|| FEATURE ITEM ||============================== //

function FeatureItem({ icon, iconColor = 'primary', title, description, delay }: FeatureItemProps) {
  const theme = useTheme();

  return (
    <motion.div
      initial={{ opacity: 0, y: 30 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ type: 'spring', stiffness: 150, damping: 30, delay }}
    >
      <Stack direction={{ xs: 'column', md: 'row' }} sx={{ gap: 1.5, alignItems: { xs: 'center', md: 'flex-start' } }}>
        <Avatar
          variant="rounded"
          type="combined"
          color={iconColor}
          sx={(theme) => ({ bgcolor: withAlpha(getColors(theme, iconColor).lighter, 0.15) })}
        >
          {icon}
        </Avatar>
        <Stack sx={{ gap: 0.25, alignItems: { xs: 'center', md: 'flex-start' } }}>
          <Typography variant="subtitle1" sx={{ textAlign: { xs: 'center', sm: 'start' } }}>
            {title}
          </Typography>
          <Typography
            variant="body2"
            sx={{ display: { xs: 'none', sm: 'block' }, color: theme.vars.palette.grey[700], textAlign: { xs: 'center', md: 'start' } }}
          >
            {description}
          </Typography>
        </Stack>
      </Stack>
    </motion.div>
  );
}

// ==============================|| INFORMATION BLOCK ||============================== //

export default function InformationBlock() {
  return (
    <ContainerWrapper>
      <Grid size={12}>
        <Stack direction="row" sx={{ gap: { xs: 2, md: 9 }, width: 1, justifyContent: { xs: 'space-around', md: 'flex-start' } }}>
          <FeatureItem
            icon={<CopyOutlined style={{ fontSize: '1rem' }} />}
            title="Auto-context injection"
            description="Pre-load your tech stack rules into every agent session."
            delay={0.3}
          />
          <FeatureItem
            icon={<SafetyCertificateOutlined style={{ fontSize: '1rem' }} />}
            iconColor="warning"
            title="Role Definition"
            description='Assign strict personas like "Senior React Engineer" globally.'
            delay={0.4}
          />
          <FeatureItem
            icon={<SlidersOutlined style={{ fontSize: '1rem' }} />}
            iconColor="info"
            title="Directory Guards"
            description="Enforce architectural boundaries across folders."
            delay={0.5}
          />
        </Stack>
      </Grid>
    </ContainerWrapper>
  );
}
