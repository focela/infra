import { Link as RouterLink } from 'react-router-dom';

// material-ui
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// third-party
import { motion } from 'framer-motion';

// project imports
import CodeEditorCard from './CodeEditorCard';
import FloatingCard from './FloatingCard';
import PerformanceCard from './PerformanceCard';
import RefactorCard from './RefactorCard';
import RefactoringTooltip from './RefactoringTooltip';
import SecurityCard from './SecurityCard';

import AnimateButton from 'components/@extended/AnimateButton';
import Avatar from 'components/@extended/Avatar';
import ContainerWrapper from 'components/ContainerWrapper';
import { ThemeDirection } from 'config';
import { withAlpha } from 'utils/colorUtils';
import getColors from 'utils/getColors';

// types
import { ColorProps } from 'types/extended';

// assets
import ThunderboltOutlined from '@ant-design/icons/ThunderboltOutlined';
import AimOutlined from '@ant-design/icons/AimOutlined';
import SlidersOutlined from '@ant-design/icons/SlidersOutlined';
import ProfileOutlined from '@ant-design/icons/ProfileOutlined';

interface FeatureItemProps {
  icon: React.ReactNode;
  title: string;
  description: string;
  delay: number;
  iconColor?: ColorProps;
}

// ==============================|| FEATURE ITEM ||============================== //

function FeatureItem({ icon, iconColor = 'primary', title, description, delay }: FeatureItemProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 30 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ type: 'spring', stiffness: 150, damping: 30, delay }}
    >
      <Stack direction={{ xs: 'column', md: 'row' }} sx={{ gap: 1.5, alignItems: { xs: 'center', md: 'flex-start' } }}>
        <Avatar
          variant="rounded"
          type="combined"
          color={iconColor}
          sx={(theme) => ({ bgcolor: withAlpha(getColors(theme, iconColor).darker, 0.15) })}
        >
          {icon}
        </Avatar>
        <Stack sx={{ gap: 0.25, alignItems: { xs: 'center', md: 'flex-start' } }}>
          <Typography variant="subtitle1" sx={{ color: 'common.white' }}>
            {title}
          </Typography>
          <Typography
            variant="body2"
            sx={{ display: { xs: 'none', sm: 'block' }, color: 'grey.400', textAlign: { xs: 'center', md: 'left' } }}
          >
            {description}
          </Typography>
        </Stack>
      </Stack>
    </motion.div>
  );
}

// ==============================|| LANDING - AI PROMPTS HERO ||============================== //

export default function AIPromptsHero() {
  const theme = useTheme();
  const downLG = useMediaQuery(theme.breakpoints.down('lg'));
  const isRTL = theme.direction === ThemeDirection.RTL;

  return (
    <ContainerWrapper sx={{ minHeight: 650, height: '100vh', display: 'flex', alignItems: 'center' }}>
      <Grid
        container
        spacing={2}
        sx={{ pt: { md: 4, xs: 8 }, pb: { md: 0, xs: 5 }, alignItems: 'center', justifyContent: 'space-between' }}
      >
        {/* Left Content */}
        <Grid size={{ xs: 12, md: 6, lg: 5 }}>
          <Grid
            container
            spacing={2}
            sx={(theme) => ({
              pr: { xs: 0, md: 4 },
              [theme.breakpoints.down('md')]: { textAlign: 'center', justifyContent: 'center', alignItems: 'center' }
            })}
          >
            {/* Badge */}
            <Grid size={12}>
              <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }}>
                <Chip
                  label={`✨ New Feature`}
                  variant="combined"
                  size="large"
                  sx={{
                    bgcolor: withAlpha(theme.vars.palette.secondary.A200!, 0.5),
                    borderColor: withAlpha(theme.vars.palette.secondary[600]!, 0.5),
                    color: 'secondary.light'
                  }}
                />
              </motion.div>
            </Grid>

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
                  <span>Master Your Workflow with Ready-made </span>
                  <Box component="span" sx={{ color: 'primary.main' }}>
                    <span>AI Prompts</span>
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
                <Typography
                  variant="h6"
                  color={theme.vars.palette.secondary.light}
                  sx={{
                    width: { xs: 1, sm: '60%', md: 1 },
                    mx: { xs: 'auto', md: 'unset' },
                    textAlign: { xs: 'center', md: 'left' },
                    fontSize: { xs: '0.875rem', md: '1rem' },
                    fontWeight: 400,
                    lineHeight: { xs: 1.4, md: 1.4 }
                  }}
                >
                  Unlock high-performance coding with our curated library of Mantis-optimized prompts. Refactor faster, maintain quality,
                  and scale instantly.
                </Typography>
              </motion.div>
            </Grid>

            {/* Feature List */}
            <Grid size={12} sx={{ mt: { xs: 5, md: 2 } }}>
              <Stack
                direction={{ xs: 'row', md: 'column' }}
                divider={<Divider orientation="vertical" flexItem sx={{ display: { xs: 'block', md: 'none' }, borderColor: 'grey.700' }} />}
                sx={{ gap: 2, width: 1, justifyContent: { xs: 'space-around', md: 'flex-start' } }}
              >
                <FeatureItem
                  icon={<ThunderboltOutlined style={{ fontSize: '1rem' }} />}
                  title="Refactor Faster"
                  description="Instant optimizations for complex legacy logic."
                  delay={0.3}
                />
                <FeatureItem
                  icon={<AimOutlined style={{ fontSize: '1rem' }} />}
                  iconColor="warning"
                  title="Mantis Specific"
                  description="Tailored prompts designed for our deep ecosystem."
                  delay={0.4}
                />
                <FeatureItem
                  icon={<SlidersOutlined style={{ fontSize: '1rem' }} />}
                  iconColor="info"
                  title="Fully Customizable"
                  description="Save and tweak prompts to match your unique style."
                  delay={0.5}
                />
              </Stack>
            </Grid>

            {/* CTA Buttons */}
            <Grid size={12} sx={{ mt: 2 }}>
              <motion.div
                initial={{ opacity: 0, y: 50 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ type: 'spring', stiffness: 150, damping: 30, delay: 0.6 }}
              >
                <Stack direction={{ xs: 'row', sm: 'row' }} sx={{ gap: 2, width: 1, justifyContent: { xs: 'center', md: 'flex-start' } }}>
                  <AnimateButton>
                    <Button
                      component={RouterLink}
                      to="https://github.com/codedthemes/mantis-free-react-admin-template"
                      target="_blank"
                      color="secondary"
                      variant="outlined"
                      sx={{
                        '&:hover': {
                          color: 'grey.100',
                          borderColor: 'grey.100',
                          ...theme.applyStyles('dark', { color: 'grey.900', borderColor: 'grey.900' })
                        }
                      }}
                    >
                      Start Free Trial
                    </Button>
                  </AnimateButton>
                  <AnimateButton>
                    <Button
                      component={RouterLink}
                      to="/ai-prompts#prompt-library"
                      size="medium"
                      variant="contained"
                      startIcon={<ProfileOutlined style={{ fontSize: '1.15rem' }} />}
                    >
                      Prompts Library
                    </Button>
                  </AnimateButton>
                </Stack>
              </motion.div>
            </Grid>
          </Grid>
        </Grid>

        {/* Right Content - Floating Cards */}
        <Grid size={{ xs: 12, md: 6, lg: 7 }} sx={{ display: { xs: 'none', md: 'block' } }}>
          <Box sx={{ position: 'relative', height: 560, width: 1 }}>
            {/* Code Editor Card - Main card in center */}
            <Box sx={{ position: 'absolute', top: 80, ...(isRTL ? { right: 0 } : { left: 0 }) }}>
              <CodeEditorCard />
            </Box>

            {/* Security Card - Top right */}
            <FloatingCard delay={0.4} duration={3.5} amplitude={12} sx={{ top: 10, ...(isRTL ? { left: 100 } : { right: 100 }) }}>
              <SecurityCard />
            </FloatingCard>

            {/* Refactor Card - Bottom left */}
            <FloatingCard
              delay={0.8}
              duration={3.8}
              amplitude={9}
              sx={{ top: 380, ...(downLG && { display: 'none' }), ...(isRTL ? { right: 80 } : { left: 80 }) }}
            >
              <RefactorCard />
            </FloatingCard>

            {/* Performance Card - Overlapping code editor, center-right */}
            <FloatingCard
              delay={0.6}
              duration={4.5}
              amplitude={10}
              sx={{ top: 360, ...(downLG && { display: 'none' }), ...(isRTL ? { left: -60 } : { right: -60 }) }}
            >
              <PerformanceCard />
            </FloatingCard>

            {/* Refactoring Tooltip - Bottom right */}
            <FloatingCard delay={1} duration={5} amplitude={6} sx={{ top: 520, ...(isRTL ? { left: -40 } : { right: -40 }) }}>
              <RefactoringTooltip />
            </FloatingCard>
          </Box>
        </Grid>
      </Grid>
    </ContainerWrapper>
  );
}
