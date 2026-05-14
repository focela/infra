// material-ui
import { useColorScheme, useTheme } from '@mui/material/styles';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// third-party
import Marquee from 'react-fast-marquee';

// project imports
import AnimateCard from 'components/@extended/AnimateCard';
import ContainerWrapper from 'components/ContainerWrapper';
import MainCard from 'components/MainCard';
import SectionTypeset from 'components/pages/SectionTypeset';

import { ThemeDirection, ThemeMode } from 'config';
import { withAlpha } from 'utils/colorUtils';

// assets
import techCI from 'assets/images/landing/technology/tech-ci.png';
import techAngular from 'assets/images/landing/technology/tech-angular.png';
import techBootstrap from 'assets/images/landing/technology/tech-bootstrap.png';
import techDotnet from 'assets/images/landing/technology/tech-dot-net.png';
import techVue from 'assets/images/landing/technology/tech-vue.png';

import techCIDark from 'assets/images/landing/technology/tech-ci-dark.png';
import techAngularDark from 'assets/images/landing/technology/tech-angular-dark.png';
import techBootstrapDark from 'assets/images/landing/technology/tech-bootstrap-dark.png';
import techDotnetDark from 'assets/images/landing/technology/tech-dot-net-dark.png';
import techVueDark from 'assets/images/landing/technology/tech-vue-dark.png';

// ================================|| SLIDER - ITEMS ||================================ //

function Item({ item }: { item: { text: string; highlight?: boolean } }) {
  const { colorScheme } = useColorScheme();
  const customShadow = colorScheme === ThemeMode.DARK ? '#000' : `${withAlpha('#000', 0.1)}`;

  return (
    <MainCard
      content={false}
      border={false}
      sx={{
        py: { xs: 1.25, sm: 2.5 },
        px: { xs: 2.5, sm: 5 },
        '&:hover': { boxShadow: `0px 4px 6px ${customShadow}`, bgcolor: 'background.paper' }
      }}
    >
      <Typography
        variant="h3"
        sx={{
          cursor: 'pointer',
          fontWeight: 500,
          transition: 'all 0.3s ease-in-out',
          opacity: item.highlight ? 0.75 : 0.4,
          '&:hover': { opacity: '1' }
        }}
      >
        {item.text}
      </Typography>
    </MainCard>
  );
}

// ==============================|| LANDING - PARTNER PAGE ||============================== //

export default function PartnerBlock() {
  const theme = useTheme();
  const { colorScheme } = useColorScheme();

  const partnerimage = [
    {
      image: colorScheme === ThemeMode.DARK ? techCIDark : techCI,
      link: 'https://codedthemes.com/item/mantis-codeigniter-admin-template/'
    },
    {
      image: colorScheme === ThemeMode.DARK ? techAngularDark : techAngular,
      link: 'https://codedthemes.com/item/mantis-angular-admin-template/'
    },
    {
      image: colorScheme === ThemeMode.DARK ? techBootstrapDark : techBootstrap,
      link: 'https://codedthemes.com/item/mantis-bootstrap-admin-dashboard/'
    },
    {
      image: colorScheme === ThemeMode.DARK ? techDotnetDark : techDotnet,
      link: 'https://codedthemes.com/item/mantis-dotnet-bootstrap-dashboard-template/'
    },
    {
      image: colorScheme === ThemeMode.DARK ? techVueDark : techVue,
      link: 'https://codedthemes.com/item/mantis-vue-admin-template/'
    }
  ];

  const items = [
    { text: 'Auth Methods' },
    { text: '128+ Pages' },
    { text: '6+ Preset Colors' },
    { text: '200+ Widgets' },
    { text: 'Best User Experience' },
    { text: 'Live Customizer' },
    { text: '5+ Apps' },
    { text: 'MUI v7' },
    { text: 'Highly Flexible' },
    { text: 'Always Updated' },
    { text: 'Professional Design' },
    { text: 'TypeScript Support' },
    { text: 'Figma Design' },
    { text: 'Dark Layout' },
    { text: 'RTL Support' },
    { text: 'Retina Ready' },
    { text: 'Prettier Code' },
    { text: 'i18n Support' }
  ];

  return (
    <ContainerWrapper>
      <MainCard content={false} sx={{ py: 5, mt: 5 }}>
        <Stack sx={{ gap: 6.25 }}>
          <SectionTypeset
            heading="Available Technology"
            description={
              <>
                Mantis is available in multiple technologies. Simply click to dive in and discover the perfect solution for your needs. Each
                is sold{' '}
                <Link href="https://codedthemes.gitbook.io/mantis/mantis-eco-system" target="_blank" underline="hover">
                  separately.
                </Link>
              </>
            }
            caption="Multiple tech stack"
            descriptionProps={{ maxWidth: 600, mx: 'auto' }}
            stackProps={{ px: 1.25 }}
          />
          <Grid container spacing={2.5} sx={{ px: 1.25, mb: '-6px', textAlign: 'center', justifyContent: 'center', flexWrap: 'wrap' }}>
            {partnerimage.map((item, index) => (
              <Grid key={index}>
                <AnimateCard variants={{ visible: { opacity: 1 }, hidden: { opacity: 0 } }}>
                  <Link href={item.link} target="_blank">
                    <Box
                      component="img"
                      src={item.image}
                      alt="feature"
                      sx={{
                        transition: 'transform 0.3s ease, opacity 0.3s ease',
                        '&:hover': {
                          transform: 'scale(1.1)',
                          opacity: 0.8
                        }
                      }}
                    />
                  </Link>
                </AnimateCard>
              </Grid>
            ))}
          </Grid>
          <Divider sx={{ borderStyle: 'dashed', borderColor: 'secondary.light' }} />

          <Box sx={{ position: 'relative' }}>
            <Grid container spacing={1.75}>
              <Grid sx={{ direction: theme.direction, overflow: 'hidden' }} size={12}>
                <Marquee pauseOnHover direction={theme.direction === ThemeDirection.RTL ? 'right' : 'left'}>
                  {items.map((item, index) => (
                    <Box sx={{ mx: 0.875 }} key={index}>
                      <Item key={index} item={item} />
                    </Box>
                  ))}
                </Marquee>
              </Grid>
              <Grid sx={{ direction: theme.direction, overflow: 'hidden' }} size={12}>
                <Marquee pauseOnHover direction={theme.direction === ThemeDirection.RTL ? 'left' : 'right'}>
                  {items.map((item, index) => (
                    <Box sx={{ mx: 0.875 }} key={index}>
                      <Item key={index} item={item} />
                    </Box>
                  ))}
                </Marquee>
              </Grid>
              <Grid sx={{ direction: theme.direction, overflow: 'hidden' }} size={12}>
                <Marquee pauseOnHover direction={theme.direction === ThemeDirection.RTL ? 'right' : 'left'}>
                  {items.map((item, index) => (
                    <Box sx={{ mx: 0.875 }} key={index}>
                      <Item key={index} item={item} />
                    </Box>
                  ))}
                </Marquee>
              </Grid>
            </Grid>
            <Box
              sx={{
                position: 'absolute',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                width: 1,
                height: 1,
                borderRadius: '50%',
                bgcolor: 'secondary.light',
                opacity: { xs: 1, sm: 0.6 },
                ...theme.applyStyles('dark', {
                  bgcolor: 'secondary.main',
                  opacity: 0.25
                }),
                filter: { xs: 'blur(170px)', sm: 'blur(250px)' }
              }}
            />
          </Box>
        </Stack>
      </MainCard>
    </ContainerWrapper>
  );
}
