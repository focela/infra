import { useEffect, useRef, useState } from 'react';

// material-ui
import { useTheme } from '@mui/material/styles';
import Button from '@mui/material/Button';
import CardMedia from '@mui/material/CardMedia';
import Chip from '@mui/material/Chip';
import Divider from '@mui/material/Divider';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

// third-party
import { motion, AnimatePresence, useScroll, useTransform } from 'framer-motion';

// project import
import Avatar from 'components/@extended/Avatar';
import MainCard from 'components/MainCard';
import ContainerWrapper from 'components/ContainerWrapper';
import { withAlpha } from 'utils/colorUtils';

// assets
import ArrowRightOutlined from '@ant-design/icons/ArrowRightOutlined';
import CheckOutlined from '@ant-design/icons/CheckOutlined';
import CrownFilled from '@ant-design/icons/CrownFilled';

// assets
import categoryTheme from 'assets/images/landing/category-theme.svg';
import categoryApplications from 'assets/images/landing/category-application.svg';
import categoryAuthentication from 'assets/images/landing/category-auth.svg';
import categoryData from 'assets/images/landing/category-data.svg';
import categoryForms from 'assets/images/landing/category-forms.svg';
import categoryLanding from 'assets/images/landing/category-landing.svg';
import categoryLayout from 'assets/images/landing/category-layout.svg';
import categoryCommon from 'assets/images/landing/category-common.svg';

interface PromptsItem {
  image: string;
  title: string;
  description: string;
  promptsNumber: number;
  delay: number;
}

// threshold - adjust threshold as needed
const options = { root: null, rootMargin: '0px', threshold: 0.6 };

const promptsItems: PromptsItem[] = [
  {
    image: categoryTheme,
    title: 'Theming',
    description: 'Color palettes, typography, directions, i18n and dark mode.',
    promptsNumber: 8,
    delay: 0.2
  },
  {
    image: categoryApplications,
    title: 'Applications',
    description: 'Ready-to-use application templates for various use cases.',
    promptsNumber: 19,
    delay: 0.4
  },
  {
    image: categoryAuthentication,
    title: 'Authentication',
    description: 'Login forms, JWT handling, and secure routes.',
    promptsNumber: 6,
    delay: 0.6
  },
  {
    image: categoryData,
    title: 'Data Fetching',
    description: 'API integration, react-query, and SWR patterns.',
    promptsNumber: 2,
    delay: 0.8
  },
  {
    image: categoryLanding,
    title: 'Landing Page',
    description: 'Pre-built landing page templates for various use cases',
    promptsNumber: 2,
    delay: 1.0
  },
  {
    image: categoryLayout,
    title: 'Layouts',
    description: 'Pre-built layouts for dashboards, admin panels, and more',
    promptsNumber: 6,
    delay: 1.2
  },
  {
    image: categoryForms,
    title: 'Forms',
    description: 'Complete form layouts, validation, and input components.',
    promptsNumber: 2,
    delay: 1.4
  },
  {
    image: categoryCommon,
    title: 'Common',
    description: 'Miscellaneous prompts for various use cases',
    promptsNumber: 4,
    delay: 1.6
  }
];

// ==============================|| AI PROMPTS HERO ||============================== //

export default function AIPromptsHero() {
  const theme = useTheme();
  const containerRef = useRef(null);

  const { scrollYProgress } = useScroll({
    target: containerRef,
    offset: ['start start', 'end start']
  });
  const scale = useTransform(scrollYProgress, [0, 0.1, 0.2, 0.4, 0.6], [0.9, 0.92, 0.94, 0.96, 1]);

  const videoRef = useRef<HTMLVideoElement>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [showProOverlay, setShowProOverlay] = useState(false);

  const proFeatures = ['Unlimited Prompt Access', 'Agents.md Integration', '8+ Prompt Categories'];

  // Handle video play/pause based on intersection with the viewport
  useEffect(() => {
    const handleIntersection = (entries: IntersectionObserverEntry[]) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          if (videoRef.current && !isPlaying) {
            videoRef.current
              .play()
              .then(() => {
                setIsPlaying(true);
              })
              .catch((error) => {
                console.error('Autoplay was prevented:', error);
              });
          }
        } else {
          if (videoRef.current && isPlaying) {
            videoRef.current.pause();
            setIsPlaying(false);
          }
        }
      });
    };

    const observer = new IntersectionObserver(handleIntersection, options);
    const videoElement = videoRef.current;

    if (videoElement) {
      observer.observe(videoElement);
    }

    return () => {
      if (videoElement) {
        observer.unobserve(videoElement);
      }
    };
  }, [isPlaying]);

  // Show Pro overlay when video ends (plays once, no loop)
  useEffect(() => {
    const video = videoRef.current;
    if (!video) return;
    const handleEnded = () => setShowProOverlay(true);
    video.addEventListener('ended', handleEnded);
    return () => video.removeEventListener('ended', handleEnded);
  }, []);

  return (
    <ContainerWrapper>
      <Box ref={containerRef}>
        <Box sx={{ pt: 6, pb: 2, px: 1 }}>
          <Stack sx={{ alignItems: 'center', gap: 2.5 }}>
            <motion.div
              initial={{ opacity: 0, scale: 0.6 }}
              whileInView={{ opacity: 1, scale: [0.6, 1.15, 0.95, 1] }}
              animate={{
                boxShadow: [
                  `0 0 0px ${withAlpha(theme.vars.palette.primary.dark, 0)}`,
                  `0 0 20px ${withAlpha(theme.vars.palette.primary.main, 0.8)}`,
                  `0 0 0px ${withAlpha(theme.vars.palette.primary.dark, 0)}`
                ],
                borderRadius: '74px'
              }}
              viewport={{ once: true }}
              transition={{ duration: 0.6, delay: 0.8, ease: 'linear' }}
            >
              <Chip
                label={`✨ AI-Powered Prompts`}
                variant="combined"
                size="large"
                sx={{
                  bgcolor: withAlpha(theme.vars.palette.secondary.A200!, 0.5),
                  borderColor: withAlpha(theme.vars.palette.secondary[600]!, 0.5),
                  color: 'secondary.light'
                }}
              />
            </motion.div>

            <motion.div
              initial={{ opacity: 0, scale: 0.6 }}
              whileInView={{ opacity: 1, scale: 1 }}
              viewport={{ once: true }}
              transition={{ duration: 0.6, delay: 0.2, ease: 'linear' }}
            >
              <Typography
                variant="h1"
                color="white"
                sx={{
                  maxWidth: '500px',
                  fontSize: { xs: '1.825rem', sm: '2rem', md: '2.5rem' },
                  fontWeight: 700,
                  lineHeight: { xs: 1.3, sm: 1.3, md: 1.3 },
                  textAlign: 'center'
                }}
              >
                <Box component="span" sx={{ color: 'primary.main' }}>
                  <span>Code Smarter, </span>
                </Box>
                <span>Not Harder with </span>
                <Box component="span" sx={{ color: 'primary.main' }}>
                  <span>Premade prompts</span>
                </Box>
              </Typography>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, translateY: 280 }}
              animate={{ opacity: 1, translateY: 0 }}
              transition={{ type: 'spring', stiffness: 150, damping: 30, delay: 0.2 }}
            >
              <Typography
                variant="h6"
                color={theme.vars.palette.secondary.light}
                sx={{
                  mt: -1.5,
                  maxWidth: '700px',
                  textAlign: 'center',
                  fontSize: { xs: '0.875rem', md: '1rem' },
                  fontWeight: 400,
                  lineHeight: { xs: 1.4, md: 1.4 }
                }}
              >
                The First Prompt Library for Mantis React Developers that understands your entire file structure, codebase and business
                logic before you type a single character.
              </Typography>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 1, delay: 0.2, ease: [0.215, 0.61, 0.355, 1] }}
            >
              <Divider sx={{ borderColor: 'primary.darker', minWidth: 280, width: '12%', my: 1 }} />
            </motion.div>

            <Stack direction="row" sx={{ maxWidth: '700px', gap: 1, flexWrap: 'wrap', justifyContent: 'center' }}>
              {promptsItems.map((item, index) => (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, scale: 0.6 }}
                  whileInView={{ opacity: 1, scale: 1 }}
                  viewport={{ once: true }}
                  transition={{ duration: 0.9, delay: index * 0.08, ease: 'linear' }}
                >
                  <Chip
                    label={item.title}
                    variant="outlined"
                    color="secondary"
                    icon={<CardMedia image={item.image} sx={{ width: 16, height: 16 }} />}
                    slotProps={{ label: { sx: { py: 0.75, px: 1.5, typography: 'caption2', color: 'secondary.light' } } }}
                    sx={{ height: 32, px: 1 }}
                  />
                </motion.div>
              ))}
            </Stack>
          </Stack>
        </Box>

        <motion.div
          initial={{ opacity: 0, scale: 0.6 }}
          whileInView={{ opacity: 1, scale: 0.9 }}
          viewport={{ once: true }}
          transition={{ duration: 0.9, delay: 0.3 }}
          style={{ scale }}
        >
          <Box sx={{ position: 'relative' }}>
            <MainCard content={false} sx={{ border: '5px solid', borderColor: 'grey.200', borderRadius: 3 }}>
              <video
                playsInline
                ref={videoRef}
                width="100%"
                height="100%"
                style={{ display: 'flex', objectFit: 'cover' }}
                preload="metadata"
                autoPlay={false}
                loop={false}
                muted={true}
              >
                <source src="https://d2elhhoq00m1pj.cloudfront.net/Mantis_AI.mp4" type="video/mp4" />
              </video>
            </MainCard>

            {/* Pro Version Overlay */}
            <AnimatePresence>
              {showProOverlay && (
                <motion.div
                  key="pro-overlay"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                  transition={{ duration: 0.6, ease: 'easeOut' }}
                  style={{
                    position: 'absolute',
                    inset: 0,
                    borderRadius: `calc(${theme.shape.borderRadius} * 3)`,
                    backdropFilter: 'blur(14px)',
                    WebkitBackdropFilter: 'blur(14px)',
                    backgroundColor: 'rgba(6, 12, 26, 0.88)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    zIndex: 10,
                    padding: '24px'
                  }}
                >
                  <motion.div
                    initial={{ opacity: 0, scale: 0.85, y: 30 }}
                    animate={{ opacity: 1, scale: 1, y: 0 }}
                    transition={{ duration: 0.5, delay: 0.2, ease: [0.215, 0.61, 0.355, 1] }}
                  >
                    {/* Pro card — fully custom Box so background gradient is never overridden by MUI Paper */}
                    <Box
                      sx={{
                        position: 'relative',
                        maxWidth: 400,
                        width: 1,
                        borderRadius: 3,
                        overflow: 'hidden',
                        border: `1.5px solid ${withAlpha(theme.vars.palette.warning.main, 0.7)}`,
                        background: 'linear-gradient(160deg, #0a1628 0%, #0f2245 55%, #0d1b35 100%)',
                        boxShadow: '0 0 48px rgba(234,179,8,0.12), 0 24px 60px rgba(0,0,0,0.55)',
                        /* decorative top-glow blob */
                        '&::before': {
                          content: '""',
                          position: 'absolute',
                          top: -80,
                          left: '50%',
                          transform: 'translateX(-50%)',
                          width: 280,
                          height: 160,
                          borderRadius: '50%',
                          background: 'rgba(234,179,8,0.09)',
                          filter: 'blur(30px)',
                          pointerEvents: 'none'
                        },
                        /* decorative bottom-left blob */
                        '&::after': {
                          content: '""',
                          position: 'absolute',
                          bottom: -50,
                          left: -50,
                          width: 160,
                          height: 160,
                          borderRadius: '50%',
                          background: 'rgba(58,130,246,0.08)',
                          filter: 'blur(24px)',
                          pointerEvents: 'none'
                        }
                      }}
                    >
                      {/* PRO ACCESS badge */}
                      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2.5 }}>
                        <Chip
                          label="PRO ACCESS"
                          color="warning"
                          size="small"
                          sx={{ fontWeight: 700, letterSpacing: 1.2, fontSize: '0.68rem', px: 0.5 }}
                        />
                      </Box>

                      <Stack sx={{ alignItems: 'center', gap: 2, px: 3, pt: 2.5, pb: 3, position: 'relative', zIndex: 1 }}>
                        {/* Crown icon */}
                        <Avatar
                          type="outlined"
                          size="lg"
                          color="warning"
                          sx={{ border: `2px solid ${withAlpha(theme.vars.palette.warning.main, 0.7)}`, bgcolor: 'transparent' }}
                        >
                          <CrownFilled style={{ fontSize: 26, color: theme.vars.palette.warning.main }} />
                        </Avatar>

                        {/* Heading */}
                        <Stack sx={{ alignItems: 'center', gap: 0.75 }}>
                          <Typography variant="h4" sx={{ color: 'common.white', textAlign: 'center', fontWeight: 700, lineHeight: 1.3 }}>
                            Unlock Full Pro Access
                          </Typography>
                          <Typography
                            variant="body2"
                            sx={{ color: withAlpha(theme.vars.palette.common.white, 0.55), textAlign: 'center', lineHeight: 1.5 }}
                          >
                            Get the complete library of 50+ advanced prompts &amp; features.
                          </Typography>
                        </Stack>

                        {/* Price */}
                        <Box
                          sx={{
                            textAlign: 'center',
                            background: withAlpha(theme.vars.palette.common.white, 0.04),
                            borderRadius: 2,
                            px: 3,
                            py: 1.25,
                            border: '1px solid',
                            borderColor: withAlpha(theme.vars.palette.common.white, 0.07),
                            width: 1
                          }}
                        >
                          <Stack direction="row" sx={{ alignItems: 'baseline', justifyContent: 'center', gap: 1 }}>
                            <Typography
                              component="span"
                              sx={{ color: withAlpha(theme.palette.common.white, 0.5), fontSize: '1.5rem', textDecoration: 'line-through' }}
                            >
                              $69
                            </Typography>
                            <Typography
                              component="span"
                              sx={{ color: theme.vars.palette.success.dark, fontWeight: 700, fontSize: '2rem', lineHeight: 1 }}
                            >
                              Free
                            </Typography>
                          </Stack>
                          <Typography
                            variant="caption"
                            sx={{ color: withAlpha(theme.palette.common.white, 0.4), display: 'block', mt: 0.5 }}
                          >
                            for Mantis Pro users · Lifetime access
                          </Typography>
                        </Box>

                        {/* Feature list */}
                        <List
                          component="ul"
                          sx={{
                            width: 1,
                            p: 0,
                            m: 0,
                            '& > li': { px: 0, py: 0.625 }
                          }}
                        >
                          {proFeatures.map((feat, i) => (
                            <ListItem
                              key={i}
                              divider={i < proFeatures.length - 1}
                              sx={{ borderColor: withAlpha(theme.palette.common.white, 0.08) }}
                            >
                              <ListItemIcon sx={{ minWidth: 28 }}>
                                <CheckOutlined style={{ color: '#4ade80', fontSize: 13 }} />
                              </ListItemIcon>
                              <ListItemText
                                primary={feat}
                                slotProps={{ primary: { sx: { fontSize: '0.8rem', color: 'rgba(255,255,255,0.72)' } } }}
                              />
                            </ListItem>
                          ))}
                        </List>

                        {/* CTA */}
                        <Button
                          variant="contained"
                          size="large"
                          color="warning"
                          endIcon={<ArrowRightOutlined />}
                          href="https://mui.com/store/items/mantis-react-admin-dashboard-template/"
                          target="_blank"
                          sx={{
                            width: 1,
                            fontWeight: 700
                          }}
                        >
                          Get Pro Access
                        </Button>

                        <Button
                          size="small"
                          onClick={() => setShowProOverlay(false)}
                          sx={{
                            color: withAlpha(theme.palette.common.white, 0.35),
                            fontSize: '0.72rem',
                            textTransform: 'none',
                            '&:hover': { color: withAlpha(theme.palette.common.white, 0.6) }
                          }}
                        >
                          Continue watching Video
                        </Button>
                      </Stack>
                    </Box>
                  </motion.div>
                </motion.div>
              )}
            </AnimatePresence>
          </Box>
        </motion.div>
      </Box>
    </ContainerWrapper>
  );
}
