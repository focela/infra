import { Link } from 'react-router-dom';

// material-ui
import { useTheme } from '@mui/material/styles';
import Button from '@mui/material/Button';
import CardMedia from '@mui/material/CardMedia';
import Grid from '@mui/material/Grid';
import MainCard from 'components/MainCard';
import Stack from '@mui/material/Stack';
import Skeleton from '@mui/material/Skeleton';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// third-party
import { motion } from 'framer-motion';

// project imports
import Avatar from 'components/@extended/Avatar';
import ContainerWrapper from 'components/ContainerWrapper';
import { withAlpha } from 'utils/colorUtils';

// assets
import CopyOutlined from '@ant-design/icons/CopyOutlined';
import AnimateButton from 'components/@extended/AnimateButton';
import CheckCircleFilled from '@ant-design/icons/CheckCircleFilled';
import ArrowRightOutlined from '@ant-design/icons/ArrowRightOutlined';
import AndroidOutlined from '@ant-design/icons/AndroidOutlined';

import imgdemo1 from 'assets/images/landing/mantis-ai.jpg';

// ==============================|| AUTOMATION SECTION ||============================== //

export default function AutomationSection() {
  const theme = useTheme();

  const checkIcon = (
    <Box component="span" sx={{ color: 'primary.main' }}>
      <CheckCircleFilled style={{ fontSize: '1.15rem' }} />
    </Box>
  );

  return (
    <Box
      sx={(theme) => ({
        position: 'relative',
        overflow: 'hidden',
        bgcolor: theme.vars.palette.grey[800],
        py: 5,
        ...theme.applyStyles('dark', { bgcolor: theme.vars.palette.grey[100] })
      })}
    >
      <ContainerWrapper>
        <Grid container spacing={5}>
          <Grid
            container
            spacing={2}
            sx={{ pt: { md: 0, xs: 8 }, pb: { md: 0, xs: 5 }, alignItems: 'center', justifyContent: 'space-between' }}
          >
            {/* Left Content */}
            <Grid size={{ xs: 12, md: 6 }}>
              <Grid
                container
                spacing={2}
                sx={(theme) => ({
                  pr: { xs: 0, md: 4 },
                  [theme.breakpoints.down('md')]: { textAlign: 'center', justifyContent: 'center', alignItems: 'center' }
                })}
              >
                {/* Heading */}
                <Grid size={12}>
                  <motion.div
                    initial={{ opacity: 0, y: 50 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ type: 'spring', stiffness: 150, damping: 30, delay: 0.1 }}
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
                      <span>Deliver faster with </span>
                      <Box component="span" sx={{ color: 'primary.main' }}>
                        <span>Mantis Prompts</span>
                      </Box>
                    </Typography>
                  </motion.div>
                </Grid>

                {/* Description */}
                <Grid size={12}>
                  <motion.div
                    initial={{ opacity: 0, y: 50 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ type: 'spring', stiffness: 150, damping: 30, delay: 0.2 }}
                  >
                    <Typography color={theme.vars.palette.grey[500]}>
                      Your project is already context aware, you don't need ask so much to get the best out of your AI assistant. Mantis Pro
                      provides you with a set of pre-built prompts that are designed to work with your codebase and file structure, so you
                      can get started right away.
                    </Typography>
                  </motion.div>
                </Grid>

                {/* Feature List */}
                <Grid size={12} sx={{ mt: { xs: 5, md: 2 } }}>
                  <Grid container spacing={2} sx={{ justifyContent: 'center' }}>
                    <Grid size={{ xs: 6, sm: 5, lg: 6 }}>
                      <Stack sx={{ gap: 2.5 }}>
                        <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
                          {checkIcon}
                          <Typography variant="h6" color="common.white">
                            Zero Configuration
                          </Typography>
                        </Stack>
                        <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
                          {checkIcon}
                          <Typography variant="h6" color="common.white">
                            Reduced Token Usage
                          </Typography>
                        </Stack>
                        <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
                          {checkIcon}
                          <Typography variant="h6" color="common.white">
                            All in your Environment
                          </Typography>
                        </Stack>
                      </Stack>
                    </Grid>
                    <Grid size={{ xs: 6, sm: 5, lg: 6 }}>
                      <Stack sx={{ gap: 2.5 }}>
                        <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
                          {checkIcon}
                          <Typography variant="h6" color="common.white">
                            Start immediately
                          </Typography>
                        </Stack>
                        <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
                          {checkIcon}
                          <Typography variant="h6" color="common.white">
                            Customizing prompt in your hand
                          </Typography>
                        </Stack>
                        <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center' }}>
                          {checkIcon}
                          <Typography variant="h6" color="common.white">
                            Reduce cost of AI agent
                          </Typography>
                        </Stack>
                      </Stack>
                    </Grid>
                  </Grid>
                </Grid>

                {/* CTA Buttons */}
                <Grid sx={{ mt: 4 }} size={12}>
                  <motion.div
                    initial={{ opacity: 0, y: 50 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ type: 'spring', stiffness: 150, damping: 30, delay: 0.6 }}
                  >
                    <Stack
                      direction={{ xs: 'row', sm: 'row' }}
                      sx={{ gap: 2, width: 1, justifyContent: { xs: 'center', md: 'flex-start' } }}
                    >
                      <AnimateButton>
                        <Button
                          size="medium"
                          variant="contained"
                          endIcon={<ArrowRightOutlined />}
                          component={Link}
                          to="https://github.com/codedthemes/mantis-free-react-admin-template"
                          target="_blank"
                        >
                          Start Coding for Free
                        </Button>
                      </AnimateButton>
                      <AnimateButton>
                        <Button
                          color="secondary"
                          variant="outlined"
                          endIcon={<CopyOutlined />}
                          component={Link}
                          to="/ai-prompts#prompt-library"
                          sx={{
                            '&:hover': {
                              color: 'grey.100',
                              borderColor: 'grey.100',
                              ...theme.applyStyles('dark', { color: 'grey.900', borderColor: 'grey.900' })
                            }
                          }}
                        >
                          View prompts Library
                        </Button>
                      </AnimateButton>
                    </Stack>
                  </motion.div>
                </Grid>
                <Grid size={12} sx={{ mt: 4 }}>
                  <motion.div
                    initial={{ opacity: 0, y: 50 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ type: 'spring', stiffness: 150, damping: 30, delay: 0.2 }}
                  >
                    <Typography color={theme.vars.palette.grey[500]}>Trusted by 3,500+ developers at top tech companies.</Typography>
                  </motion.div>
                </Grid>
              </Grid>
            </Grid>

            {/* Right Content - Floating Cards */}
            <Grid size={{ xs: 12, md: 6 }} sx={{ display: { xs: 'none', md: 'flex' }, alignItems: 'center', justifyContent: 'center' }}>
              <MainCard
                content={false}
                sx={{
                  bgcolor: withAlpha(theme.vars.palette.grey[700], 0.98),
                  width: { xs: 420, sm: 520, lg: 780 },
                  borderColor: theme.vars.palette.grey[700],
                  backdropFilter: 'blur(20px)',
                  boxShadow: `0 25px 50px ${withAlpha(theme.vars.palette.common.black, 0.4)}`,
                  borderRadius: 2,
                  overflow: 'hidden',
                  position: 'relative',
                  p: 0,
                  '& .MuiCardContent-root': {
                    p: 0
                  },
                  ...theme.applyStyles('dark', {
                    bgcolor: theme.vars.palette.secondary[100],
                    borderColor: withAlpha(theme.vars.palette.secondary.darker, 0.05)
                  })
                }}
              >
                {/* Window Title Bar */}
                <Stack
                  direction="row"
                  sx={{
                    gap: 0.75,
                    alignItems: 'center',
                    px: 2,
                    py: 1.5,
                    bgcolor: withAlpha(theme.vars.palette.grey[800], 0.6),
                    borderBottom: `1px solid ${withAlpha(theme.vars.palette.grey[600], 0.2)}`,
                    ...theme.applyStyles('dark', {
                      bgcolor: withAlpha(theme.vars.palette.secondary.darker, 0.05),
                      borderColor: withAlpha(theme.vars.palette.secondary.darker, 0.05)
                    })
                  }}
                >
                  <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: theme.vars.palette.error.main }} />
                  <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: theme.vars.palette.warning.main }} />
                  <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: theme.vars.palette.success.main }} />
                  <Typography
                    variant="caption"
                    sx={{ color: theme.vars.palette.grey[400], pl: 1.5, flexGrow: 1, textAlign: 'center', pr: 6 }}
                    noWrap
                  >
                    Agent.md
                  </Typography>
                </Stack>

                {/* Code content */}
                <Stack
                  direction="column"
                  sx={{
                    flex: 1,
                    display: 'flex',
                    position: 'relative',
                    bgcolor: withAlpha(theme.vars.palette.grey[900], 0.5),
                    overflow: 'hidden',
                    '& pre': {
                      background: 'transparent !important'
                    },
                    ...theme.applyStyles('dark', { bgcolor: withAlpha(theme.vars.palette.grey[100], 0.98) })
                  }}
                >
                  <CardMedia
                    component="img"
                    src={imgdemo1}
                    alt="imgdemo1"
                    sx={{
                      width: 1,
                      height: 1,
                      objectFit: 'cover',
                      borderRadiusTopRight: 3,
                      borderRadiusTopLeft: 3
                    }}
                  />
                  <Box
                    sx={{
                      position: 'absolute',
                      bottom: 40,
                      right: 40,
                      width: 0.9,
                      p: 2.5,
                      borderRadius: 3,
                      bgcolor: 'rgba(25, 30, 48, 0.8)',
                      backdropFilter: 'blur(12px)',
                      border: '1px solid',
                      borderColor: withAlpha(theme.vars.palette.primary.main, 0.2),
                      boxShadow: '0 8px 32px rgba(0, 0, 0, 0.2)',
                      ...theme.applyStyles('dark', { borderColor: theme.vars.palette.primary[200] })
                    }}
                  >
                    <Stack direction="row" sx={{ gap: 1.5, alignItems: 'center', pb: 2.5 }}>
                      <Avatar
                        sx={{
                          bgcolor: withAlpha(theme.vars.palette.primary.main, 0.2),
                          borderRadius: 1.5,
                          alignItems: 'center',
                          justifyContent: 'center'
                        }}
                      >
                        <AndroidOutlined style={{ fontSize: '1.5rem' }} />
                      </Avatar>
                      <Stack sx={{ flex: 1, gap: 1 }}>
                        <Skeleton
                          animation={false}
                          width="40%"
                          variant="text"
                          height={10}
                          sx={{
                            bgcolor: withAlpha(theme.vars.palette.primary.main, 0.2),
                            ...theme.applyStyles('dark', { bgcolor: theme.vars.palette.primary[200] })
                          }}
                        />
                        <Skeleton
                          animation={false}
                          width="70%"
                          variant="text"
                          height={10}
                          sx={{
                            bgcolor: withAlpha(theme.vars.palette.primary.main, 0.2),
                            ...theme.applyStyles('dark', { bgcolor: theme.vars.palette.primary[200] })
                          }}
                        />
                        <Skeleton
                          animation={false}
                          width="50%"
                          variant="text"
                          height={10}
                          sx={{
                            bgcolor: withAlpha(theme.vars.palette.primary.main, 0.2),
                            ...theme.applyStyles('dark', { bgcolor: theme.vars.palette.primary[200] })
                          }}
                        />
                      </Stack>
                    </Stack>
                    <Stack
                      sx={{
                        display: 'inline-flex',
                        px: 1.5,
                        py: 0.5,
                        borderRadius: 1,
                        bgcolor: withAlpha(theme.vars.palette.primary.main, 0.2),
                        color: theme.vars.palette.primary.main,
                        fontSize: '0.875rem',
                        fontWeight: 500
                      }}
                    >
                      Generating
                    </Stack>
                  </Box>
                </Stack>
              </MainCard>
            </Grid>
          </Grid>
        </Grid>
      </ContainerWrapper>
    </Box>
  );
}
