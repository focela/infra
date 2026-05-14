import { Link } from 'react-router-dom';

// material-ui
import CardMedia from '@mui/material/CardMedia';
import Divider from '@mui/material/Divider';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// project imports
import AnimateCard from 'components/@extended/AnimateCard';
import MainCard from 'components/MainCard';

// assets
import ArrowRightOutlined from '@ant-design/icons/ArrowRightOutlined';

interface CategoryCardProps {
  image: string;
  title: string;
  description: string;
  promptsNumber: number;
  delay: number;
  animationVariants?: any;
}

// ==============================|| CATEGORY - CATEGORY CARD ||============================== //

export default function CategoryCard({ image, title, description, promptsNumber, animationVariants }: CategoryCardProps) {
  const variants = animationVariants || ({ hidden: { opacity: 0, translateY: 280 }, visible: { opacity: 1, translateY: 0 } } as any);

  return (
    <AnimateCard variants={variants}>
      <MainCard contentSX={{ p: 3 }} sx={{ height: 1 }}>
        <Stack alignItems="flex-start" sx={{ gap: 1.25, width: 1 }}>
          <CardMedia component="img" sx={{ width: 'auto' }} src={image} alt="prompts" />
          <Typography variant="h5" sx={{ fontWeight: 500, mt: 1.25 }}>
            {title}
          </Typography>
          <Typography variant="body1" color="secondary">
            {description}
          </Typography>
        </Stack>
        <Divider sx={{ pt: 3, width: '100%' }} />
        <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ pt: 3 }}>
          <Typography variant="h6" sx={{ fontWeight: 600 }}>
            {promptsNumber} prompts
          </Typography>
          <Box sx={{ color: 'grey.500' }} component={Link} to="/ai-prompts#prompt-library" target="_blank">
            <ArrowRightOutlined />
          </Box>
        </Stack>
      </MainCard>
    </AnimateCard>
  );
}
