// material-ui
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import MainCard from 'components/MainCard';
import SectionTypeset from 'components/pages/SectionTypeset';
import SyntaxHighlight from 'utils/SyntaxHighlight';
import { withAlpha } from 'utils/colorUtils';
import { ThemeDirection } from 'config';

// third-party
import { motion } from 'framer-motion';

// assets
import BulbFilled from '@ant-design/icons/BulbFilled';
import SettingFilled from '@ant-design/icons/SettingFilled';

const codeString = `# Mantis React TypeScript Admin Template - Complete AI Instructions

## Role & Context
You are a 'Frontend Template Architect'. Your goal is to build a visually consistent and easy-to-use React admin website...

## Project Overview
 - Mantis is a Material-UI-based admin dashboard template with two variants:
  1. full-version (/full-version): All features, components, and integrations
  2. seed (/seed): Minimal scaffold for custom development
  ...

## Tech Stack
- **Framework**: React (Functional Components)
`;

// ==============================|| FLOATING CARD ||============================== //

function FloatingCard({
  title,
  description,
  icon,
  color,
  sx,
  delay = 0
}: {
  title: string;
  description: string;
  icon: React.ReactNode;
  color: 'primary' | 'success';
  sx?: object;
  delay?: number;
}) {
  const theme = useTheme();

  return (
    <motion.div
      initial={{ y: 0 }}
      animate={{ y: [-10, 10, -10] }}
      transition={{
        duration: 6,
        repeat: Infinity,
        repeatType: 'reverse',
        ease: 'easeInOut',
        delay: delay
      }}
      style={{ position: 'absolute', zIndex: 1, ...sx }}
    >
      <MainCard
        content={false}
        sx={{
          width: 280,
          bgcolor: withAlpha(theme.palette.secondary.darker, 0.85),
          border: `1px solid ${withAlpha(theme.vars.palette.common.white, 0.1)}`,
          p: 2,
          boxShadow: theme.customShadows.z1
        }}
      >
        <Stack direction="row" spacing={2} alignItems="flex-start">
          <Stack
            sx={{
              width: 40,
              height: 40,
              borderRadius: 1,
              bgcolor: withAlpha(theme.vars.palette[color].main, 0.15),
              alignItems: 'center',
              justifyContent: 'center',
              color: `${color}.main`
            }}
          >
            {icon}
          </Stack>
          <Stack sx={{ gap: 0.5 }}>
            <Typography variant="subtitle1" color="white">
              {title}
            </Typography>
            <Typography variant="caption" sx={{ color: theme.vars.palette.grey[500], lineHeight: 1.5 }}>
              {description}
            </Typography>
          </Stack>
        </Stack>
      </MainCard>
    </motion.div>
  );
}

// ==============================|| CODE EDITOR CARD ||============================== //

export default function AgentBlock() {
  const theme = useTheme();
  const downLG = useMediaQuery(theme.breakpoints.down('lg'));
  const isRTL = theme.direction === ThemeDirection.RTL;

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
        <Grid container spacing={2} sx={{ alignItems: 'center', justifyContent: 'center' }}>
          <Grid size={12}>
            <Grid container spacing={1} sx={{ mb: 4, textAlign: 'center', justifyContent: 'center' }}>
              <Grid size={{ sm: 10, md: 6 }}>
                <SectionTypeset
                  caption="Power Under The Hood"
                  heading="Super charged with Agents.md"
                  description="Once loaded, your own AI model will automatically index your entire codebase and file structure, making it context aware."
                  headingProps={{
                    sx: {
                      color: 'common.white'
                    }
                  }}
                />
              </Grid>
            </Grid>
          </Grid>
          <Box sx={{ position: 'relative' }}>
            {/* Left Bottom Card - Reasoning */}
            <FloatingCard
              title="Reasoning"
              description="Advanced logic for complex refactors."
              icon={<BulbFilled style={{ fontSize: 20 }} />}
              color="success"
              sx={{ bottom: 60, ...(downLG && { display: 'none' }), ...(isRTL ? { right: -60 } : { left: -150 }) }}
              delay={0}
            />

            {/* Right Top Card - Context Injection */}
            <FloatingCard
              title="Context Injection"
              description="Agents automatically index your entire repo."
              icon={<SettingFilled style={{ fontSize: 20 }} />}
              color="primary"
              sx={{ top: 100, ...(downLG && { display: 'none' }), ...(isRTL ? { left: -60 } : { right: -190 }) }}
              delay={1.5}
            />

            <MainCard
              content={false}
              sx={{
                bgcolor: withAlpha(theme.vars.palette.grey[700], 0.98),
                width: { xs: 350, sm: 520, lg: 780 },
                borderColor: theme.vars.palette.grey[600],
                backdropFilter: 'blur(20px)',
                boxShadow: `0 25px 50px ${withAlpha(theme.vars.palette.common.black, 0.4)}`,
                borderRadius: 2,
                overflow: 'hidden',
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
                  bgcolor: withAlpha(theme.vars.palette.grey[900], 0.5),
                  overflow: 'hidden',
                  '& pre': {
                    background: 'transparent !important'
                  },
                  ...theme.applyStyles('dark', { bgcolor: withAlpha(theme.vars.palette.grey[100], 0.98) })
                }}
              >
                <SyntaxHighlight language="markdown" wrapLongLines={true} darkStyle>
                  {codeString}
                </SyntaxHighlight>
              </Stack>
            </MainCard>
          </Box>
        </Grid>
      </ContainerWrapper>
    </Box>
  );
}
