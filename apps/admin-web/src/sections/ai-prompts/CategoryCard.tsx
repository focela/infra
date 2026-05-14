// material-ui
import CardMedia from '@mui/material/CardMedia';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// project imports
import AnimateCard from 'components/@extended/AnimateCard';
import MainCard from 'components/MainCard';

interface CategoryCardProps {
  image: string;
  title: string;
  description: string;
  promptsNumber: number;
  freePrompts?: string;
  delay: number;
  animationVariants?: any;
}

// ==============================|| CATEGORY - CATEGORY CARD ||============================== //

export default function CategoryCard({ image, title, description, promptsNumber, freePrompts, animationVariants }: CategoryCardProps) {
  const variants = animationVariants || ({ hidden: { opacity: 0, translateY: 280 }, visible: { opacity: 1, translateY: 0 } } as any);

  return (
    <AnimateCard variants={variants} style={{ height: '100%' }}>
      <MainCard contentSX={{ p: 3, display: 'flex', flexDirection: 'column', height: 1 }} sx={{ height: 1, position: 'relative' }}>
        <Stack direction="row" alignItems="center" sx={{ gap: 1.25 }}>
          <CardMedia component="img" sx={{ width: 'auto' }} src={image} alt="prompts" />
          <Typography variant="h4" sx={{ fontWeight: 600, display: 'flex', alignItems: 'flex-end', gap: 0.5 }}>
            {promptsNumber}
            <Typography variant="caption" sx={{ color: 'text.secondary' }}>
              prompts
            </Typography>
          </Typography>
        </Stack>
        <Stack sx={{ mt: 1.25, gap: 1.25, flexGrow: 1, width: 1, alignItems: 'flex-start', justifyContent: 'space-between' }}>
          <Box>
            <Typography variant="h5">{title}</Typography>
            <Typography variant="body1" color="secondary">
              {description}
            </Typography>
          </Box>
          {freePrompts && <Chip label={freePrompts} variant="combined" color="success" size="small" />}
        </Stack>
      </MainCard>
    </AnimateCard>
  );
}
