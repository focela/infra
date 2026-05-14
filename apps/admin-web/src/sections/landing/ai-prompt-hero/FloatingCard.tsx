import { ReactNode } from 'react';

// third-party
import { motion } from 'framer-motion';

interface FloatingCardProps {
  children: ReactNode;
  delay?: number;
  duration?: number;
  amplitude?: number;
  sx?: object;
}

// ==============================|| FLOATING CARD COMPONENT ||============================== //

export default function FloatingCard({ children, delay = 0, duration = 3, amplitude = 10, sx = {} }: FloatingCardProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 80, scale: 0.9 }}
      animate={{
        opacity: 1,
        y: [0, -amplitude, 0],
        x: [0, amplitude * 0.3, 0, -amplitude * 0.3, 0],
        rotate: [0, 1, 0, -1, 0],
        scale: 1
      }}
      transition={{
        opacity: { duration: 0.8, delay, ease: [0.25, 0.46, 0.45, 0.94] },
        scale: { duration: 0.8, delay, ease: [0.25, 0.46, 0.45, 0.94] },
        y: {
          duration,
          repeat: Infinity,
          ease: 'easeInOut',
          delay: delay + 0.8
        },
        x: {
          duration: duration * 1.5,
          repeat: Infinity,
          ease: 'easeInOut',
          delay: delay + 0.8
        },
        rotate: {
          duration: duration * 2,
          repeat: Infinity,
          ease: 'easeInOut',
          delay: delay + 0.8
        }
      }}
      style={{ position: 'absolute', ...sx }}
    >
      {children}
    </motion.div>
  );
}
