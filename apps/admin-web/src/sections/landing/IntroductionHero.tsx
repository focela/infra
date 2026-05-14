import { Link as RouterLink } from 'react-router-dom';

// material-ui
import { useTheme } from '@mui/material/styles';
import Button from '@mui/material/Button';
import CardMedia from '@mui/material/CardMedia';
import Chip from '@mui/material/Chip';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// third-party
import { motion, AnimatePresence } from 'framer-motion';

// project imports
import AnimateButton from 'components/@extended/AnimateButton';
import ContainerWrapper from 'components/ContainerWrapper';
import { withAlpha } from 'utils/colorUtils';
import useConfig from 'hooks/useConfig';
import { getImageUrl, ImagePath } from 'utils/getImageUrl';

// assets
import EyeOutlined from '@ant-design/icons/EyeOutlined';
import headertechimg from 'assets/images/landing/img-headertech.svg';

// ==============================|| LANDING - HEADER PAGE ||============================== //

export default function HeaderPage() {
  const theme = useTheme();

  const { state } = useConfig();

  return (
    <Box sx={{ position: 'relative', height: '100%', width: '100%', overflow: 'hidden' }}>
      <AnimatePresence>
        <motion.div
          initial={{ opacity: 0, scale: 1.1, x: 100 }}
          animate={{ opacity: 1, scale: 1, x: 0 }}
          exit={{ opacity: 0, scale: 0.95, x: 150 }}
          transition={{
            duration: 0.8,
            ease: [0.25, 0.46, 0.45, 0.94]
          }}
          style={{
            position: 'absolute',
            width: '100%',
            height: '100%',
            zIndex: 1
          }}
        >
          <CardMedia
            component="img"
            image={getImageUrl(`bg-mockup-${state.presetColor}.png`, ImagePath.LANDING)}
            alt="Mantis"
            loading="eager"
            sx={{
              position: 'absolute',
              width: { md: '78%', lg: '70%', xl: '65%' },
              right: { md: '-14%', lg: '-4%', xl: '-2%' },
              top: { md: '16%', lg: '12%', xl: '8%' },
              display: { xs: 'none', md: 'block' }
            }}
          />
        </motion.div>
      </AnimatePresence>
      <ContainerWrapper sx={{ minHeight: 650, height: '100vh', display: 'flex', alignItems: 'center', position: 'relative', zIndex: 2 }}>
        <Grid
          container
          spacing={2}
          sx={{ pt: { md: 0, xs: 8 }, pb: { md: 0, xs: 5 }, alignItems: 'center', justifyContent: 'space-between' }}
        >
          <Grid size={{ xs: 12, lg: 5, md: 6 }}>
            <Grid
              container
              spacing={2}
              sx={(theme) => ({
                pr: { xs: 0, md: 10 },
                [theme.breakpoints.down('md')]: { textAlign: 'center', justifyContent: 'center', alignItems: 'center' }
              })}
            >
              <Grid size={12}>
                <Chip
                  label={`✨ ${import.meta.env.VITE_APP_VERSION} Released`}
                  variant="combined"
                  size="large"
                  sx={{
                    bgcolor: withAlpha(theme.vars.palette.secondary.A200!, 0.5),
                    borderColor: withAlpha(theme.vars.palette.secondary[600]!, 0.5),
                    color: 'secondary.light'
                  }}
                />
              </Grid>
              <Grid size={12}>
                <motion.div
                  initial={{ opacity: 0, translateY: 280 }}
                  animate={{ opacity: 1, translateY: 0 }}
                  transition={{ type: 'spring', stiffness: 150, damping: 30 }}
                >
                  <Typography
                    variant="h1"
                    color="white"
                    sx={{
                      fontSize: { xs: '1.825rem', sm: '2rem', md: '2.5rem' },
                      fontWeight: 700,
                      lineHeight: { xs: 1.3, sm: 1.3, md: 1.3 }
                    }}
                  >
                    <span>Carefully Crafted for your </span>
                    <Box component="span" sx={{ color: 'primary.main' }}>
                      <span>Caring React </span>
                    </Box>
                    <span>Project</span>
                  </Typography>
                </motion.div>
              </Grid>
              <Grid size={12}>
                <motion.div
                  initial={{ opacity: 0, translateY: 280 }}
                  animate={{ opacity: 1, translateY: 0 }}
                  transition={{ type: 'spring', stiffness: 150, damping: 30, delay: 0.2 }}
                >
                  <Typography
                    variant="h6"
                    color={theme.vars.palette.secondary.light}
                    sx={{ fontSize: { xs: '0.875rem', md: '1rem' }, fontWeight: 400, lineHeight: { xs: 1.4, md: 1.4 } }}
                  >
                    Mantis React is a blazing-fast dashboard template built using the MUI React library.
                  </Typography>
                </motion.div>
              </Grid>
              <Grid sx={{ my: 3.25 }} size={12}>
                <motion.div
                  initial={{ opacity: 0, translateY: 280 }}
                  animate={{ opacity: 1, translateY: 0 }}
                  transition={{ type: 'spring', stiffness: 150, damping: 30, delay: 0.4 }}
                >
                  <Grid container spacing={2} sx={{ justifyContent: { xs: 'center', md: 'flex-start' } }}>
                    <Grid>
                      <AnimateButton>
                        <Button
                          component={RouterLink}
                          to="https://github.com/codedthemes/mantis-free-react-admin-template"
                          target="_blank"
                          size="large"
                          color="primary"
                          variant="outlined"
                        >
                          Get Free Version
                        </Button>
                      </AnimateButton>
                    </Grid>
                    <Grid>
                      <AnimateButton>
                        <Button
                          component={RouterLink}
                          to="/login"
                          target="_blank"
                          size="large"
                          color="primary"
                          variant="contained"
                          startIcon={<EyeOutlined style={{ fontSize: '1.15rem' }} />}
                        >
                          Live Preview
                        </Button>
                      </AnimateButton>
                    </Grid>
                  </Grid>
                </motion.div>
              </Grid>
              <Grid size={{ xs: 'auto', md: 12 }}>
                <motion.div
                  initial={{ opacity: 0, translateY: 280 }}
                  animate={{ opacity: 1, translateY: 0 }}
                  transition={{ type: 'spring', stiffness: 150, damping: 30, delay: 0.6 }}
                >
                  <CardMedia component="img" sx={{ width: 'auto' }} src={headertechimg} alt="Mantis" style={{ zIndex: 9 }} />
                </motion.div>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
      </ContainerWrapper>
    </Box>
  );
}
