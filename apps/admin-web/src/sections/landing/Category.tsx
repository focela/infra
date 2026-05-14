// material-ui
import Grid from '@mui/material/Grid';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import SectionTypeset from 'components/pages/SectionTypeset';

// third-party
import CategoryCard from './CategoryCard';

// assets
import categoryTheme from 'assets/images/landing/category-theme.svg';
import categoryApplications from 'assets/images/landing/category-application.svg';
import categoryAuthentication from 'assets/images/landing/category-auth.svg';
import categoryData from 'assets/images/landing/category-data.svg';
import categoryForms from 'assets/images/landing/category-forms.svg';
import categoryLanding from 'assets/images/landing/category-landing.svg';
import categoryLayout from 'assets/images/landing/category-layout.svg';
import categoryCommon from 'assets/images/landing/category-common.svg';

interface promptsItem {
  image: string;
  title: string;
  description: string;
  promptsNumber: number;
  delay: number;
}

const promptsItems: promptsItem[] = [
  {
    image: categoryTheme,
    title: 'Theming',
    description: 'Color palettes, typography, directions, i18n and dark mode.',
    promptsNumber: 8,
    delay: 0.2
  },
  {
    image: categoryApplications,
    title: 'Applications',
    description: 'Ready-to-use application templates for various use cases.',
    promptsNumber: 19,
    delay: 0.4
  },
  {
    image: categoryAuthentication,
    title: 'Authentication',
    description: 'Login forms, JWT handling, and secure routes.',
    promptsNumber: 6,
    delay: 0.6
  },
  {
    image: categoryData,
    title: 'Data Fetching',
    description: 'API integration, react-query, and SWR patterns.',
    promptsNumber: 2,
    delay: 0.8
  },
  {
    image: categoryLanding,
    title: 'Landing Page',
    description: 'Pre-built landing page templates for various use cases',
    promptsNumber: 2,
    delay: 1.0
  },
  {
    image: categoryLayout,
    title: 'Layouts',
    description: 'Pre-built layouts for dashboards, admin panels, and more',
    promptsNumber: 6,
    delay: 1.2
  },
  {
    image: categoryForms,
    title: 'Forms',
    description: 'Complete form layouts, validation, and input components.',
    promptsNumber: 2,
    delay: 1.4
  },
  {
    image: categoryCommon,
    title: 'Common',
    description: 'Miscellaneous prompts for various use cases',
    promptsNumber: 4,
    delay: 1.6
  }
];

// ==============================|| LANDING - CATEGORY BLOCK PAGE ||============================== //

export default function CategoryBlock() {
  return (
    <ContainerWrapper>
      <Grid container spacing={1} sx={{ mb: 3.5, textAlign: 'center', justifyContent: 'center' }}>
        <Grid size={{ sm: 10, md: 6 }}>
          <SectionTypeset
            caption="Multiple categories, endless possibilities"
            heading="Categories of AI Prompts"
            description="Explore a wide range of AI prompts across various categories, designed to help you build and enhance your applications with ease."
          />
        </Grid>
      </Grid>
      <Grid container spacing={2.5} sx={{ justifyContent: 'center' }}>
        {promptsItems.map((item, index) => (
          <Grid key={index} size={{ xs: 12, sm: 6, md: 3 }}>
            <CategoryCard {...item} />
          </Grid>
        ))}
      </Grid>
    </ContainerWrapper>
  );
}
