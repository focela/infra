import { useEffect, useMemo, useState, ChangeEvent, Suspense, lazy } from 'react';

// material-ui
import { useColorScheme, useTheme } from '@mui/material/styles';
import FormControlLabel from '@mui/material/FormControlLabel';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import Slide from '@mui/material/Slide';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';

// project imports
import MainCard from 'components/MainCard';
import IconButton from 'components/@extended/IconButton';

import useConfig from 'hooks/useConfig';
import { ThemeDirection, ThemeMode } from 'config';
import { withAlpha } from 'utils/colorUtils';

// third-party
import { presetDarkPalettes, presetPalettes, PalettesProps } from '@ant-design/colors';

// assets
import CheckOutlined from '@ant-design/icons/CheckOutlined';

// types
import { PresetColor } from 'types/config';

interface ColorProps {
  id: PresetColor;
  primary: string;
}

const HeroCarousel = lazy(() => import('sections/landing/HeroCarousel'));
const NumberBlock = lazy(() => import('sections/landing/NumberBlock'));
const BrowserBlock = lazy(() => import('sections/landing/BrowserBlock'));
const CallToAction = lazy(() => import('sections/landing/CallToAction'));
const ContactBanner = lazy(() => import('sections/landing/ContactBanner'));
const FeatureBlock = lazy(() => import('sections/landing/FeatureBlock'));
const FigmaBlock = lazy(() => import('sections/landing/FigmaBlock'));
const DemoBlock = lazy(() => import('sections/landing/DemoBlock'));
const TestimonialBlock = lazy(() => import('sections/landing/TestimonialBlock'));
const ElementBlock = lazy(() => import('sections/landing/ElementBlock'));
const PartnerBlock = lazy(() => import('sections/landing/PartnerBlock'));
const CategoryBlock = lazy(() => import('sections/landing/Category'));
const CodeSection = lazy(() => import('sections/landing/CodeSection'));

// Prefetch critical routes using Vite's prefetch
const prefetchRoutes = () => {
  // Use dynamic imports to trigger Vite's prefetching
  // These will be prefetched when the landing page loads
  import('pages/auth/jwt/login');
  import('pages/auth/jwt/register');

  import('pages/dashboard/analytics');
  import('pages/dashboard/default');

  import('pages/apps/e-commerce/products');
  import('pages/apps/invoice/dashboard');
  import('pages/apps/kanban');
  import('pages/apps/calendar');
  import('pages/apps/chat');

  import('pages/widget/chart');
  import('pages/widget/data');
  import('pages/widget/statistics');
};

// ==============================|| LANDING PAGE ||============================== //

export default function Landing() {
  const theme = useTheme();
  const { colorScheme } = useColorScheme();
  const { state, setField } = useConfig();

  const [visible, setVisible] = useState(false);

  useEffect(() => {
    // Prefetch routes after initial render
    prefetchRoutes();
  }, []);

  useEffect(() => {
    const listenToScroll = () => {
      let heightToHideFrom = 250;
      const winScroll = document.body.scrollTop || document.documentElement.scrollTop;

      if (winScroll > heightToHideFrom) {
        setVisible(true);
      } else {
        if (visible) setVisible(false);
      }
    };

    window.addEventListener('scroll', listenToScroll);
    return () => window.removeEventListener('scroll', listenToScroll);
  }, [visible]);

  const colors: PalettesProps = colorScheme === ThemeMode.DARK ? presetDarkPalettes : presetPalettes;
  const { blue } = colors;

  const colorOptions = useMemo<ColorProps[]>(() => {
    const isDark = colorScheme === ThemeMode.DARK;
    return [
      { id: 'theme1', primary: isDark ? '#305bdd' : '#3366FF' },
      { id: 'theme2', primary: isDark ? '#655ac8' : '#7265E6' },
      { id: 'theme3', primary: isDark ? '#0a7d3e' : '#068e44' },
      { id: 'theme4', primary: isDark ? '#5d7dcb' : '#3c64d0' },
      { id: 'default', primary: blue[5] },
      { id: 'theme5', primary: isDark ? '#d26415' : '#f27013' },
      { id: 'theme6', primary: isDark ? '#288d99' : '#2aa1af' },
      { id: 'theme7', primary: isDark ? '#05934c' : '#00a854' },
      { id: 'theme8', primary: isDark ? '#058478' : '#009688' }
    ];
  }, [colorScheme, blue]);

  const handlePresetColorChange = (event: ChangeEvent<HTMLInputElement>) => {
    setField('presetColor', event.target.value as PresetColor);
  };

  return (
    <Stack sx={{ gap: { xs: 7.5, sm: 12.5 } }}>
      <Box
        sx={(theme) => ({
          position: 'relative',
          bgcolor: colorScheme === ThemeMode.DARK ? 'grey.0' : 'grey.800',
          overflow: 'hidden',
          minHeight: '100vh',
          '& .MuiContainer-root': {
            position: 'relative',
            zIndex: 5
          },
          '&:before': {
            content: '""',
            position: 'absolute',
            width: '100%',
            height: '100%',
            top: 0,
            left: 0,
            zIndex: 2,
            background:
              theme.direction === ThemeDirection.RTL
                ? {
                    xs: 'linear-gradient(-360.36deg, rgb(0, 0, 0) 14.79%, rgba(67, 67, 67, 0.28) 64.86%)',
                    md: 'linear-gradient(-329.36deg, rgb(0, 0, 0) 1.79%, rgba(67, 67, 67, 0.28) 64.86%)',
                    xl: 'linear-gradient(-329.36deg, rgb(0, 0, 0) 1.79%, rgba(67, 67, 67, 0.28) 64.86%)'
                  }
                : 'linear-gradient(329.36deg, rgb(0, 0, 0) 14.79%, rgba(67, 67, 67, 0.28) 64.86%)'
          }
        })}
      >
        <HeroCarousel />
      </Box>

      {/* By wrapping each section (like <FeatureBlock />) in a 'Suspense' boundary with lazily loaded imports: 
      The code for these sections is split into separate chunks and not loaded until needed. 
      This makes the main page load much faster. */}
      <Suspense fallback={null}>
        <FeatureBlock />
      </Suspense>

      <Suspense fallback={null}>
        <CodeSection />
      </Suspense>

      <Suspense fallback={null}>
        <CategoryBlock />
      </Suspense>

      <Suspense fallback={null}>
        <DemoBlock />
      </Suspense>

      <Suspense fallback={null}>
        <FigmaBlock />
      </Suspense>

      <Suspense fallback={null}>
        <BrowserBlock />
      </Suspense>

      <Suspense fallback={null}>
        <NumberBlock />
      </Suspense>

      <Suspense fallback={null}>
        <ElementBlock />
      </Suspense>

      <Suspense fallback={null}>
        <PartnerBlock />
      </Suspense>

      <Suspense fallback={null}>
        <CallToAction />
      </Suspense>

      <Suspense fallback={null}>
        <TestimonialBlock />
      </Suspense>

      <Suspense fallback={null}>
        <ContactBanner />
      </Suspense>

      <Slide direction={theme.direction === ThemeDirection.RTL ? 'right' : 'left'} in={visible} mountOnEnter unmountOnExit>
        <MainCard
          sx={{
            width: { xs: '100%', sm: 'auto' },
            position: 'fixed',
            zIndex: 9,
            right: { xs: 0, sm: 16 },
            bottom: { xs: 0, sm: '25%' },
            borderRadius: { xs: 0, sm: 1 }
          }}
          content={false}
          boxShadow
          border={false}
        >
          <RadioGroup
            sx={{ alignItems: { xs: 'center', sm: 'flex-end' }, p: 1.25 }}
            aria-label="payment-card"
            name="payment-card"
            value={state.presetColor}
            onChange={handlePresetColorChange}
          >
            <Stack direction={{ xs: 'row', sm: 'column' }} sx={{ gap: 1, alignItems: 'center' }}>
              {colorOptions.map((color, index) => {
                const isSelected = state.presetColor === color.id;

                return (
                  <FormControlLabel
                    key={index}
                    control={<Radio value={color.id} sx={{ opacity: 0, position: 'absolute', zIndex: 9 }} />}
                    sx={{
                      mr: 0,
                      ml: 0,
                      zIndex: 1,
                      '&:hover': {
                        position: 'relative',
                        '& .MuiIconButton-root:after': {
                          content: '""',
                          position: 'absolute',
                          top: 0,
                          left: 0,
                          bottom: 0,
                          right: 0,
                          opacity: 0.3,
                          boxShadow: `0 0 6px 6px ${withAlpha(color.primary, 0.9)}`
                        }
                      }
                    }}
                    label={
                      <IconButton
                        shape="rounded"
                        variant="contained"
                        sx={{ bgcolor: color.primary, width: isSelected ? 28 : 20, height: isSelected ? 28 : 20 }}
                      >
                        {isSelected && <CheckOutlined />}
                      </IconButton>
                    }
                  />
                );
              })}
            </Stack>
          </RadioGroup>
        </MainCard>
      </Slide>
    </Stack>
  );
}
