import { useState, useEffect } from 'react';

// material-ui
import Box from '@mui/material/Box';

// third-party
import { motion, AnimatePresence } from 'framer-motion';

// project imports
import AIPromptsHero from './ai-prompt-hero';
import IntroductionHero from './IntroductionHero';

const SLIDE_DURATION = 8000; // 8 seconds per slide

const fadeVariants = {
  enter: {
    opacity: 0,
    scale: 0.95
  },
  center: {
    opacity: 1,
    scale: 1
  },
  exit: {
    opacity: 0,
    scale: 1.05
  }
};

// ==============================|| HERO CAROUSEL ||============================== //

export default function HeroCarousel() {
  const [activeIndex, setActiveIndex] = useState(0);
  const [[page, direction], setPage] = useState([0, 0]);

  const heroes = [
    { id: 'ai-prompts', component: AIPromptsHero },
    { id: 'introduction', component: IntroductionHero }
  ];

  // Auto-advance slides
  useEffect(() => {
    const timer = setInterval(() => {
      setActiveIndex((prev) => (prev + 1) % heroes.length);
      setPage(([prevPage]) => [prevPage + 1, 1]);
    }, SLIDE_DURATION);

    return () => clearInterval(timer);
  }, [heroes.length]);

  const CurrentHero = heroes[activeIndex].component;

  return (
    <Box sx={{ position: 'relative', width: '100%', height: '100vh', minHeight: 650, overflow: 'hidden' }}>
      <AnimatePresence custom={direction} mode="wait">
        <motion.div
          key={page}
          custom={direction}
          variants={fadeVariants}
          initial="enter"
          animate="center"
          exit="exit"
          transition={{
            x: { type: 'spring', stiffness: 300, damping: 30 },
            opacity: { duration: 0.5 },
            scale: { duration: 0.5 }
          }}
          style={{ position: 'absolute', width: '100%', height: '100%' }}
        >
          <CurrentHero />
        </motion.div>
      </AnimatePresence>

      {/* Slide Indicators */}
      <Box sx={{ position: 'absolute', bottom: 40, left: '50%', transform: 'translateX(-50%)', display: 'flex', gap: 1.5, zIndex: 10 }}>
        {heroes.map((hero, index) => {
          const isActive = activeIndex === index;
          return (
            <Box
              key={hero.id}
              onClick={() => {
                const newDirection = index > activeIndex ? 1 : -1;
                setActiveIndex(index);
                setPage([page + 1, newDirection]);
              }}
              sx={{
                width: isActive ? 32 : 8,
                height: 8,
                borderRadius: 4,
                bgcolor: isActive ? 'primary.main' : 'grey.600',
                cursor: 'pointer',
                transition: 'all 0.3s ease',
                '&:hover': {
                  bgcolor: isActive ? 'primary.main' : 'grey.500'
                }
              }}
            />
          );
        })}
      </Box>
    </Box>
  );
}
