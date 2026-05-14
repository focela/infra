import { Link } from 'react-router-dom';

// material-ui
import { useTheme } from '@mui/material/styles';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import CardMedia from '@mui/material/CardMedia';
import Chip from '@mui/material/Chip';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// third-party
import { motion } from 'framer-motion';

// project imports
import Avatar from 'components/@extended/Avatar';
import ContainerWrapper from 'components/ContainerWrapper';
import MainCard from 'components/MainCard';
import SectionTypeset from 'components/pages/SectionTypeset';
import { withAlpha } from 'utils/colorUtils';

// assets
import ArrowRightOutlined from '@ant-design/icons/ArrowRightOutlined';
import CheckOutlined from '@ant-design/icons/CheckOutlined';
import CodeOutlined from '@ant-design/icons/CodeOutlined';
import CrownFilled from '@ant-design/icons/CrownFilled';

import imgdemo1 from 'assets/images/landing/cleanup-app.jpg';
import imgdemo2 from 'assets/images/landing/global-rebrand.jpg';

interface FreePromptsType {
  image?: string;
  title: string;
  description: string;
  promptText: string;
  tag: string;
  animationVariants?: any;
  proList?: string[];
}

// ==============================|| AI PROMPTS - FREE SAMPLES DATA ||============================== //

const freeSamplesData: FreePromptsType[] = [
  {
    image: imgdemo1,
    tag: 'Free',
    title: 'Cleanup App',
    description: 'This prompt is designed to remove all unused applications from the project..',
    promptText: 'I want to specifically focus on the ...'
  },
  {
    image: imgdemo2,
    tag: 'Free',
    title: 'Change Branding',
    description: 'The "Global Rebrand" Prompt This prompt completely rebrands the application by ...',
    promptText: 'Your goal is to rebrand this application ...'
  },
  {
    tag: 'Pro',
    title: 'Unlock 50+ Pro Prompts',
    description: 'Access the full library of advanced patterns, system designs, and full-stack templates.',
    promptText: 'Includes lifetime updates & support',
    proList: ['Advanced system design patterns', 'Full-stack templates & hooks', 'Priority updates & new prompts']
  }
];

// ==============================|| AI PROMPTS - FREE SAMPLES DATA ||============================== //

function FreePromptsCard({ image, title, description, promptText, tag, proList, animationVariants }: FreePromptsType) {
  const theme = useTheme();

  const variants = animationVariants || {
    hidden: { opacity: 0, y: 40 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.5 } }
  };

  return (
    <Box sx={{ '&>div': { height: 1 }, height: 1 }}>
      <motion.div variants={variants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
        <MainCard contentSX={{ p: 0, '&:last-child': { pb: 0 } }} sx={{ height: 1, overflow: 'hidden', '&>div': { height: 1 } }}>
          {tag === 'Free' ? (
            <Box>
              <CardMedia
                component="img"
                src={image}
                alt={title}
                sx={{ width: 1, height: 1, objectFit: 'cover', borderRadiusTopRight: 3, borderRadiusTopLeft: 3 }}
              />
              <Box sx={{ p: 3 }}>
                <Stack sx={{ gap: 1 }}>
                  <Typography variant="h4">{title}</Typography>
                  <Typography variant="body1" color="secondary">
                    {description}
                  </Typography>
                </Stack>
                <Divider sx={{ pt: 2.5, mb: 2.5, width: 1 }} />
                <Alert variant="border" color="primary" icon={<CodeOutlined />}>
                  {promptText}
                </Alert>
              </Box>
            </Box>
          ) : (
            <Box
              sx={{
                position: 'relative',
                height: 1,
                overflow: 'hidden',
                background: 'linear-gradient(160deg, #0a1628 0%, #132f4c 100%)',
                '&::before': {
                  content: '""',
                  position: 'absolute',
                  top: -60,
                  right: -60,
                  width: 180,
                  height: 180,
                  borderRadius: '50%',
                  background: 'rgba(58, 130, 246, 0.12)'
                },
                '&::after': {
                  content: '""',
                  position: 'absolute',
                  bottom: -40,
                  left: -40,
                  width: 140,
                  height: 140,
                  borderRadius: '50%',
                  background: 'rgba(96, 165, 250, 0.08)'
                }
              }}
            >
              <Stack
                direction="column"
                sx={{
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  height: 1,
                  p: 3,
                  position: 'relative',
                  zIndex: 1
                }}
              >
                {/* Badge */}
                <Chip
                  label="PRO ACCESS"
                  color="warning"
                  size="small"
                  sx={{ fontWeight: 700, letterSpacing: 1, fontSize: '0.7rem', mb: 1.5 }}
                />

                {/* Icon */}
                <Avatar
                  type="outlined"
                  size="lg"
                  color="warning"
                  sx={{ border: `2px solid ${withAlpha(theme.vars.palette.warning.main, 0.7)}`, bgcolor: 'transparent' }}
                >
                  <CrownFilled style={{ fontSize: 26, color: theme.vars.palette.warning.main }} />
                </Avatar>

                {/* Title & Description */}
                <Typography variant="h4" sx={{ color: 'common.white', textAlign: 'center' }}>
                  {title}
                </Typography>
                <Typography
                  variant="body2"
                  sx={{ color: withAlpha(theme.vars.palette.common.white, 0.65), maxWidth: 280, textAlign: 'center', mt: 1 }}
                >
                  {description}
                </Typography>

                {/* Feature List */}
                <List
                  component="ul"
                  sx={{
                    width: 1,
                    my: 2.5,
                    p: 0,
                    '& > li': {
                      px: 0,
                      py: 0.75,
                      '& svg': { fill: theme.vars.palette.success.main }
                    }
                  }}
                >
                  {proList?.map((feature, index) => (
                    <ListItem
                      key={index}
                      divider={index < (proList?.length || 0) - 1}
                      sx={{ borderColor: withAlpha(theme.vars.palette.common.white, 0.1) }}
                    >
                      <ListItemIcon sx={{ minWidth: 32 }}>
                        <CheckOutlined style={{ color: theme.vars.palette.success.main }} />
                      </ListItemIcon>
                      <ListItemText
                        primary={feature}
                        slotProps={{ primary: { variant: 'body2', sx: { color: withAlpha(theme.vars.palette.common.white, 0.75) } } }}
                      />
                    </ListItem>
                  ))}
                </List>

                {/* CTA */}
                <Button
                  variant="contained"
                  color="warning"
                  size="large"
                  endIcon={<ArrowRightOutlined />}
                  component={Link}
                  to="https://github.com/codedthemes/mantis-free-react-admin-template"
                  target="_blank"
                  sx={{ width: 1 }}
                >
                  Get Pro Access
                </Button>
                <Typography variant="caption" sx={{ color: withAlpha(theme.vars.palette.common.white, 0.75), mt: 1.5 }}>
                  {promptText}
                </Typography>
              </Stack>
            </Box>
          )}
        </MainCard>
      </motion.div>
    </Box>
  );
}

// ==============================|| AI PROMPTS - FREE SAMPLES SECTION ||============================== //

export default function FreeSamplesSection() {
  return (
    <ContainerWrapper>
      <Grid container spacing={2} justifyContent="center">
        <Grid size={12} sx={{ textAlign: 'center', mb: 5 }}>
          <SectionTypeset
            caption="Explore the Possibilities"
            heading="Free Samples"
            description="Get a taste of what's possible before upgrading to Pro."
          />
        </Grid>

        <Grid container spacing={2} sx={{ justifyContent: 'center', alignItems: 'stretch' }}>
          {freeSamplesData.map((item, index) => (
            <Grid key={index} size={{ xs: 12, sm: 6, md: 4 }}>
              <FreePromptsCard {...item} />
            </Grid>
          ))}
        </Grid>
      </Grid>
    </ContainerWrapper>
  );
}
