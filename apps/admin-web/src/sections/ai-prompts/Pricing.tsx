// material-ui
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

// third-party
import { motion } from 'framer-motion';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import MainCard from 'components/MainCard';
import SectionTypeset from 'components/pages/SectionTypeset';

// assets
import CheckOutlined from '@ant-design/icons/CheckOutlined';

// assets
import categoryTheme from 'assets/images/landing/category-theme.svg';
import categoryApplications from 'assets/images/landing/category-application.svg';
import categoryAuthentication from 'assets/images/landing/category-auth.svg';
import categoryData from 'assets/images/landing/category-data.svg';
import categoryForms from 'assets/images/landing/category-forms.svg';
import categoryLanding from 'assets/images/landing/category-landing.svg';
import categoryLayout from 'assets/images/landing/category-layout.svg';
import categoryCommon from 'assets/images/landing/category-common.svg';
import CategoryCard from './CategoryCard';

interface promptsItem {
  image: string;
  title: string;
  description: string;
  promptsNumber: number;
  freePrompts?: string;
  delay: number;
}

const promptsItems: promptsItem[] = [
  {
    image: categoryTheme,
    title: 'Theming',
    description: 'Color palettes, typography, directions, i18n and dark mode.',
    promptsNumber: 8,
    freePrompts: '1 Free Prompt',
    delay: 0.2
  },
  {
    image: categoryApplications,
    title: 'Applications',
    description: 'Ready-to-use application templates for various use cases.',
    promptsNumber: 19,
    freePrompts: '1 Free Prompt',
    delay: 0.4
  },
  {
    image: categoryAuthentication,
    title: 'Authentication',
    description: 'Login forms, JWT handling, and secure routes.',
    promptsNumber: 6,
    freePrompts: '1 Free Prompt',
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
    freePrompts: '1 Free Prompt',
    delay: 1.0
  },
  {
    image: categoryLayout,
    title: 'Layouts',
    description: 'Pre-built layouts for dashboards, admin panels, and more',
    promptsNumber: 6,
    freePrompts: '2 Free Prompts',
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
    freePrompts: '1 Free Prompt',
    delay: 1.6
  }
];

const planList = [
  'Unlimited Prompt Access',
  'Agents.md Integration',
  'Advance Chain-of-Thought',
  'Constantly adding New Prompts',
  'Custom logic support',
  'Private Community Access',
  'Performance Optimization',
  '8+ Prompts Categories',
  'Advanced Prompt Engineering'
];

// ==============================|| AI PROMPTS - PRICING CARD ||============================== //

function PricingCard() {
  return (
    <MainCard sx={{ borderWidth: 3, borderColor: 'primary.main', position: 'relative', overflow: 'visible' }}>
      <Chip
        label="PRO ACCESS"
        color="primary"
        sx={{ position: 'absolute', top: 0, left: '50%', transform: 'translate(-50%, -50%)', fontSize: '0.75rem' }}
      />
      <Stack sx={{ gap: 2 }}>
        <Typography variant="h6">Full library</Typography>
        <Typography variant="h2">Free for Mantis Pro Users </Typography>
        <Typography sx={{ mt: -1.5, color: 'text.secondary' }}>Everything you need to automate your workflow completely.</Typography>
        <Button variant="contained" href="https://mui.com/store/items/mantis-react-admin-dashboard-template/" target="_blank">
          Unlock 50+ Pro Prompts
        </Button>
        <List
          component="ul"
          sx={(theme) => ({ m: 0, p: 0, '&> li': { px: 0, py: 0.625, '& svg': { fill: theme.vars.palette.success.dark } } })}
        >
          {planList.map((list, i) => (
            <ListItem key={i} divider>
              <ListItemIcon>
                <CheckOutlined />
              </ListItemIcon>
              <ListItemText primary={list} />
            </ListItem>
          ))}
        </List>
      </Stack>
    </MainCard>
  );
}

// ==============================|| AI PROMPTS - PRICING ||============================== //

export default function Pricing() {
  return (
    <ContainerWrapper>
      <Grid container spacing={2}>
        <Grid size={12}>
          <Grid container spacing={1} sx={{ mb: 4, textAlign: 'center', justifyContent: 'center' }}>
            <Grid size={{ sm: 10, md: 6 }}>
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.5 }}
              >
                <SectionTypeset
                  caption="Free for Mantis Pro users"
                  heading="Buy now to get full access to all prompts and features"
                  description=""
                />
              </motion.div>
            </Grid>
          </Grid>
        </Grid>
        <Grid size={{ xs: 12, md: 4 }}>
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            whileInView={{ opacity: 1, scale: 1 }}
            viewport={{ once: true }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            <PricingCard />
          </motion.div>
        </Grid>
        <Grid size={{ xs: 12, md: 8 }}>
          <Grid container spacing={2.5}>
            {promptsItems.map((item, index) => (
              <Grid key={index} size={{ xs: 12, sm: 4 }}>
                <CategoryCard {...item} />
              </Grid>
            ))}
          </Grid>
        </Grid>
      </Grid>
    </ContainerWrapper>
  );
}
