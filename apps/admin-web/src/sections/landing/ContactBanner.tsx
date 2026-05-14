import { Link } from 'react-router-dom';

// material-ui
import { useTheme } from '@mui/material/styles';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import SectionTypeset from 'components/pages/SectionTypeset';
import { withAlpha } from 'utils/colorUtils';

// ==============================|| LANDING - CONTACT BANNER PAGE ||============================== //

export default function ContactBanner() {
  const theme = useTheme();

  const lineColor = withAlpha(theme.vars.palette.common.white, 0.5);
  const fillColor = withAlpha(theme.vars.palette.primary.main, 0.5);

  return (
    <ContainerWrapper sx={{ mb: { xs: 7.5, sm: 15 } }}>
      <Box
        sx={{
          position: 'relative',
          overflow: 'hidden',
          borderRadius: 2,
          px: { xs: 2.5, sm: 'auto' },
          py: 5,
          bgcolor: (t) => t.palette.primary[900] ?? t.palette.primary.darker
        }}
      >
        <Box
          sx={{
            position: 'absolute',
            inset: 0,
            pointerEvents: 'none',
            '&:before, &:after': {
              content: '""',
              position: 'absolute',
              width: { xs: 150, sm: 200, md: 250, lg: 350 },
              height: { xs: 150, sm: 200, md: 250, lg: 350 },
              backgroundImage: `
                  radial-gradient(circle at 50% 50%, ${fillColor} 0%, ${fillColor} 50%, transparent 100%),
                  repeating-linear-gradient(0deg, ${lineColor} 0 1px, transparent 1px 30px),
                  repeating-linear-gradient(90deg, ${lineColor} 0 1px, transparent 1px 30px)
                `,
              maskImage: 'radial-gradient(closest-side, rgba(0,0,0,.6), transparent)',
              WebkitMaskImage: 'radial-gradient(closest-side, rgba(0,0,0,.6), transparent)'
            },
            '&:before': { top: { xs: -40, sm: -80, md: -70, lg: -160 }, left: { xs: -20, sm: -40, md: -50, lg: -110 } },
            '&:after': { bottom: { xs: -40, sm: -90, md: -115, lg: -195 }, right: { xs: -20, sm: 30, md: 65, lg: 65 } }
          }}
        />

        <Stack sx={{ gap: 2.5, textAlign: 'center', alignItems: 'center' }}>
          <SectionTypeset
            heading="Ready to fasten your development?"
            description="Join thousands of engineers and developers using Mantis Pro to ship reliable products using AI."
            headingProps={{ sx: { color: 'common.white' } }}
            descriptionProps={{ sx: { color: withAlpha(theme.vars.palette.common.white, 0.6), fontWeight: 400, maxWidth: 500 } }}
          />

          <Stack direction="row" sx={{ gap: 2, mt: 4, justifyContent: 'center' }}>
            <Button
              component={Link}
              to="https://mui.com/store/items/mantis-react-admin-dashboard-template/"
              target="_blank"
              size="large"
              variant="contained"
              color="primary"
            >
              Get Mantis Pro
            </Button>
            <Button
              component={Link}
              to="https://codedthemes.com/contact/"
              target="_blank"
              size="large"
              variant="outlined"
              sx={{ borderColor: theme.vars.palette.secondary.light, color: 'common.white' }}
            >
              Contact Sales
            </Button>
          </Stack>
        </Stack>
      </Box>
    </ContainerWrapper>
  );
}
