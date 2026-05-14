import { Link as RouterLink } from 'react-router-dom';

// material-ui
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Link from '@mui/material/Link';

// project imports
import DemoCard from './DemoCard';
import AnimateButton from 'components/@extended/AnimateButton';
import ContainerWrapper from 'components/ContainerWrapper';
import SectionTypeset from 'components/pages/SectionTypeset';

// assets
import SendOutlined from '@ant-design/icons/SendOutlined';
import imgdemo1 from 'assets/images/landing/img-demo1.jpg';
import imgdemo2 from 'assets/images/landing/img-demo2.jpg';
import imgdemo3 from 'assets/images/landing/img-demo3.jpg';

// ==============================|| LANDING - DEMO PAGE ||============================== //

export default function DemoBlock() {
  return (
    <ContainerWrapper>
      <Grid container spacing={2} sx={{ alignItems: 'center', justifyContent: 'center' }}>
        <Grid size={12}>
          <Grid container spacing={1} sx={{ mb: 4, textAlign: 'center', justifyContent: 'center' }}>
            <Grid size={{ sm: 10, md: 6 }}>
              <SectionTypeset
                heading="Complete Combo"
                description="Whether you are a developer or designer, Mantis serves the needs of all - No matter you are a novice or an expert."
                caption="Mantis for all"
              />
            </Grid>
          </Grid>
        </Grid>

        <Grid container sx={{ alignItems: 'start' }}>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <DemoCard
              title="Design Source File"
              description="You can preview the Mantis design in Figma. The Figma file is available with the Plus and Extended Licenses only."
              action={
                <AnimateButton>
                  <Button
                    variant="outlined"
                    endIcon={<SendOutlined />}
                    size="large"
                    component={Link}
                    href="https://links.codedthemes.com/dAAOP"
                    target="_blank"
                  >
                    Preview Figma
                  </Button>
                </AnimateButton>
              }
              image={imgdemo2}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <DemoCard
              title="Components"
              description=" Explore all Mantis components in one place with a search feature to make development faster and easier."
              action={
                <AnimateButton>
                  <Button size="large" variant="contained" component={RouterLink} to="/components-overview/buttons" target="_blank">
                    View All Components
                  </Button>
                </AnimateButton>
              }
              image={imgdemo1}
            />
          </Grid>
          <Grid size={{ xs: 12, sm: 6, md: 4 }}>
            <DemoCard
              title="Documentation"
              description={
                'From quick start to detailed installation, our developer-friendly documentation makes it easy to find solutions to your queries.'
              }
              action={
                <AnimateButton>
                  <Button variant="outlined" size="large" component={Link} href="https://codedthemes.gitbook.io/mantis/" target="_blank">
                    Explore Documentation
                  </Button>
                </AnimateButton>
              }
              image={imgdemo3}
            />
          </Grid>
        </Grid>
      </Grid>
    </ContainerWrapper>
  );
}
