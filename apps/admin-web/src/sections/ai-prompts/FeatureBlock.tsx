// material-ui
import { useTheme } from '@mui/material/styles';
import CardMedia from '@mui/material/CardMedia';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import MainCard from 'components/MainCard';
import SectionTypeset from 'components/pages/SectionTypeset';

// third-party
import { motion } from 'framer-motion';

// assets
import imgdemo1 from 'assets/images/landing/kanban-filter.jpg';
import imgdemo2 from 'assets/images/landing/course-management-system.jpg';
import imgdemo3 from 'assets/images/landing/switch-auth provider.jpg';

interface FeatureCaseItem {
  id: number;
  image: string;
  title: string;
  description: string;
  isPro: boolean;
  animationVariants?: any;
}

// ==============================|| AI PROMPTS - FEATURED CRAD DATA ||============================== //

const featureCaseData: FeatureCaseItem[] = [
  {
    id: 1,
    image: imgdemo1,
    title: 'Kanban Activity Filter',
    description: 'Helps users build a dynamic Kanban board with activity tracking, filtering, and task blocking.',
    isPro: true
  },
  {
    id: 2,
    image: imgdemo2,
    title: 'Course Management System',
    description: 'This prompt helps users manage course content and structure to align with their business type.',
    isPro: false
  },
  {
    id: 3,
    image: imgdemo3,
    title: 'Switch Auth Provider',
    description: 'This prompt helps users switch authentication providers to align with their business type.',
    isPro: true
  }
];

// ==============================|| FEATURE - FEATURE CASE CARD ||============================== //

function FeatureCaseCard({ image, title, description, isPro, animationVariants }: FeatureCaseItem) {
  const theme = useTheme();

  const variants = animationVariants || {
    hidden: { opacity: 0, y: 40 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.5 } }
  };

  return (
    <motion.div variants={variants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
      <MainCard contentSX={{ p: 0, '&:last-child': { pb: 1 } }} sx={{ height: 1, overflow: 'hidden' }}>
        {/* Image Preview Section */}
        <Box sx={{ position: 'relative', pb: 1.5 }}>
          {/* PRO Badge */}
          {isPro && (
            <Chip
              label="PRO"
              size="small"
              sx={{
                position: 'absolute',
                top: 12,
                right: 12,
                zIndex: 1,
                fontWeight: 700,
                fontSize: '0.65rem',
                height: 22,
                bgcolor: theme.vars.palette.secondary[800],
                color: 'common.white',
                border: `1px solid ${theme.vars.palette.secondary[600]}`,
                ...theme.applyStyles('dark', { bgcolor: theme.vars.palette.grey[100], border: `1px solid ${theme.vars.palette.grey[200]}` })
              }}
            />
          )}

          {/* Screenshot Image */}
          <CardMedia
            component="img"
            src={image}
            alt={title}
            sx={{
              width: 1,
              height: 1,
              objectFit: 'cover',
              borderRadiusTopRight: 3,
              borderRadiusTopLeft: 3
            }}
          />
        </Box>

        {/* Content Section */}
        <Stack sx={{ px: 2.5, pt: 2, pb: 1.5, gap: 1 }}>
          <Typography variant="h4">{title}</Typography>
          <Typography variant="body1" color="secondary">
            {description}
          </Typography>
        </Stack>
      </MainCard>
    </motion.div>
  );
}

// ==============================|| AI PROMPTS - FEATURED USE CASE BLOCK ||============================== //

export default function FeatureBlock() {
  return (
    <ContainerWrapper>
      <Grid container spacing={2} sx={{ alignItems: 'center', justifyContent: 'center' }}>
        <Grid size={12}>
          <Grid container spacing={1} sx={{ mb: 4, textAlign: 'center', justifyContent: 'center' }}>
            <Grid size={{ sm: 10, md: 6 }}>
              <SectionTypeset
                caption="Powering the next generation of AI prompts"
                heading="Featured Use Cases"
                description="Top-rated prompt collections for enterprise workflows."
              />
            </Grid>
          </Grid>
        </Grid>

        <Grid container spacing={3} sx={{ alignItems: 'stretch' }}>
          {featureCaseData.map((item) => (
            <Grid key={item.id} size={{ xs: 12, sm: 6, md: 4 }}>
              <FeatureCaseCard {...item} />
            </Grid>
          ))}
        </Grid>
      </Grid>
    </ContainerWrapper>
  );
}
