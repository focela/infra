// material-ui
import Grid from '@mui/material/Grid';

// project imports
import FeatureCard from './FeatureCard';
import ContainerWrapper from 'components/ContainerWrapper';
import SectionTypeset from 'components/pages/SectionTypeset';

// assets
import imgfeature1 from 'assets/images/landing/img-feature1.svg';
import imgfeature2 from 'assets/images/landing/img-feature2.svg';
import imgfeature3 from 'assets/images/landing/img-feature3.svg';
import imgfeature4 from 'assets/images/landing/img-feature4.svg';

const features = [
  {
    image: imgfeature4,
    title: 'AI Prompts',
    description: 'Production-grade AI prompt library to ship faster.'
  },
  {
    image: imgfeature1,
    title: 'Professional Design',
    description: 'Mantis has a fully professional-grade user interface for any kind of backend projects.'
  },
  {
    image: imgfeature2,
    title: 'Flexible Solution',
    description: 'Highly flexible to work around using the Mantis React Template.'
  },
  {
    image: imgfeature3,
    title: 'Effective Documentation',
    description: 'Need help? Check out the detailed Documentation guide.'
  }
];

// ==============================|| LANDING - FEATURE PAGE ||============================== //

export default function FeatureBlock() {
  return (
    <ContainerWrapper>
      <Grid container spacing={1} sx={{ mb: 3.5, textAlign: 'center', justifyContent: 'center' }}>
        <Grid size={{ sm: 10, md: 6 }}>
          <SectionTypeset
            caption="Mantis nailed it!"
            heading="Why Mantis?"
            description="Customize everything with the Mantis React Material-UI Dashboard Template, built with the latest MUI v7 component library."
          />
        </Grid>
      </Grid>
      <Grid container spacing={2.5} sx={{ justifyContent: 'center' }}>
        {features.map((feature, index) => (
          <Grid key={index} size={{ xs: 12, sm: 6, md: 3 }}>
            <FeatureCard {...feature} />
          </Grid>
        ))}
      </Grid>
    </ContainerWrapper>
  );
}
