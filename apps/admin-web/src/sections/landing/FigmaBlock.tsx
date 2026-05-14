// material-ui
import { useTheme } from '@mui/material/styles';
import Button from '@mui/material/Button';
import CardMedia from '@mui/material/CardMedia';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import SectionTypeset from 'components/pages/SectionTypeset';
import useConfig from 'hooks/useConfig';
import { getImageUrl, ImagePath } from 'utils/getImageUrl';

// assets
import ArrowRightOutlined from '@ant-design/icons/ArrowRightOutlined';

import figmaIcon from 'assets/images/icons/landing-figma.svg';
import figmaBackground from 'assets/images/landing/figma-background.png';

// =============================|| LANDING - FIGMA PAGE ||============================= //

export default function FigmaBlock() {
  const theme = useTheme();
  const { state } = useConfig();

  return (
    <Box
      sx={(theme) => ({
        position: 'relative',
        overflow: 'hidden',
        bgcolor: 'secondary.800',
        py: 5,
        ...theme.applyStyles('dark', { bgcolor: 'grey.100' })
      })}
    >
      <ContainerWrapper>
        <Stack sx={{ alignItems: 'center', gap: 6 }}>
          <Stack gap={4}>
            <SectionTypeset
              heading="The Mantis Figma UI kit & Design system"
              description="Optimize your workflow with 100% Auto Layout, variables, smart variants, and WCAG accessibility. Enjoy super-smart global colors, typography, and effects styles + variables, crafted for seamless design efficiency!"
              headingProps={{ sx: { color: 'common.white' } }}
              descriptionProps={{ sx: { textAlign: 'center', maxWidth: 585 } }}
            />
            <Stack direction="row" gap={2} sx={{ justifyContent: 'center' }}>
              <Button
                component={Link}
                variant="outlined"
                href="https://www.figma.com/community/file/1527646378189493961/mantis-free-dashboard-kit"
                startIcon={<img src={figmaIcon} alt="Figma" />}
                target="_blank"
              >
                Try the Free Kit
              </Button>
              <Button
                component={Link}
                variant="contained"
                href="https://codedthemes.com/item/mantis-figma-ui-kit/"
                endIcon={<ArrowRightOutlined />}
                target="_blank"
              >
                Upgrade to Pro
              </Button>
            </Stack>
          </Stack>
          <Stack sx={{ position: 'relative', width: 1, alignItems: 'center' }}>
            <CardMedia component="img" src={figmaBackground} alt="Mantis figma back" />
            <Box sx={{ position: 'absolute', bottom: { xs: 15, sm: 25, md: 40, lg: 45 }, width: '64%' }}>
              <CardMedia
                component="img"
                src={getImageUrl(`figma-front-${state.presetColor}.webp`, ImagePath.LANDING)}
                alt="Mantis Figma Dashboard"
                sx={{ zIndex: 2 }}
              />
            </Box>
          </Stack>
        </Stack>
      </ContainerWrapper>
      <Box
        sx={{
          position: 'absolute',
          bottom: 0,
          left: 0,
          right: 0,
          height: '240px',
          background: `linear-gradient(to top, ${theme.palette.secondary.dark} 24%, ${theme.palette.secondary.dark} 0%,transparent 100%)`
        }}
      />
    </Box>
  );
}
