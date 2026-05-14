// material-ui lab
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Box from '@mui/material/Box';

import Timeline from '@mui/lab/Timeline';
import TimelineItem from '@mui/lab/TimelineItem';
import TimelineSeparator from '@mui/lab/TimelineSeparator';
import TimelineConnector from '@mui/lab/TimelineConnector';
import TimelineContent from '@mui/lab/TimelineContent';
import TimelineDot from '@mui/lab/TimelineDot';
import TimelineOppositeContent from '@mui/lab/TimelineOppositeContent';

// third-party
import { motion } from 'framer-motion';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import SectionTypeset from 'components/pages/SectionTypeset';
import { withAlpha } from 'utils/colorUtils';

interface WorkflowStep {
  title: string;
  description: string;
}

interface WorkflowTimelineItemProps {
  item: WorkflowStep;
  index: number;
  count: number;
}

const steps: WorkflowStep[] = [
  {
    title: 'Initialize Agents.md',
    description: 'Your model read the Agents.md file to outline specific roles, responsibilities, and structure for your project.'
  },
  {
    title: 'Select Prompt Template',
    description: 'You select prompt from our library based on your need.'
  },
  {
    title: 'Your Model starts working',
    description:
      'The engine absorbs your codebase context, reading existing utilities and style tokens. This ensures the new code looks and behaves exactly like your legacy code.'
  },
  {
    title: 'Generate Code',
    description:
      'Watch as the model produces optimized, commented, and type-safe code. It handles boilerplate, imports, and error handling automatically.'
  },
  {
    title: 'Deploy & Iterate',
    description: 'Review the output in the diff viewer. Accept changes with one click, and push to production with confidence.'
  }
];

const MotionTimelineItem = motion.create(TimelineItem);

// ==============================|| WORKFLOW - TIMELINE ITEM ||============================== //

function WorkflowTimelineItem({ item, index, count }: WorkflowTimelineItemProps) {
  const theme = useTheme();

  const isEven = index % 2 === 0;
  const isFirst = index === 0;
  const isLast = index === count - 1;

  return (
    <MotionTimelineItem
      sx={{ minHeight: 120 }}
      initial={{ opacity: 0, y: 20 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true }}
      transition={{ duration: 0.5, delay: index * 0.1 }}
    >
      <TimelineOppositeContent
        align={isEven ? 'right' : undefined}
        variant="body2"
        color="secondary.lighter"
        sx={{ height: 1, alignSelf: 'center', display: { xs: 'none', md: 'block' } }}
      ></TimelineOppositeContent>
      <TimelineSeparator sx={{ mx: 3 }}>
        <TimelineConnector
          sx={{
            ...(isFirst
              ? { bgcolor: 'transparent' }
              : { bgcolor: 'secondary.dark', ...theme.applyStyles('dark', { bgcolor: 'secondary.200' }) }),
            flexGrow: 1,
            display: { xs: 'none', md: 'block' }
          }}
        />
        <TimelineDot
          sx={{
            bgcolor: 'transparent',
            border: '3px solid',
            borderColor: 'primary.main',
            borderRadius: '50%',
            height: 16,
            width: 16,
            m: 2,
            boxShadow: `0 0 0 6px ${withAlpha(theme.vars.palette.primary.main, 0.25)}`
          }}
        />
        <TimelineConnector
          sx={{
            ...(isLast
              ? { bgcolor: 'transparent' }
              : { bgcolor: 'secondary.dark', ...theme.applyStyles('dark', { bgcolor: 'secondary.200' }) }),
            flexGrow: 1
          }}
        />
      </TimelineSeparator>
      <TimelineContent
        sx={{
          alignSelf: { xs: 'flex-start', md: 'center' },
          display: 'flex',
          flexDirection: { xs: 'column', md: 'row' },
          alignItems: { xs: 'flex-start', md: 'center' },
          justifyContent: { xs: 'flex-start', md: isEven ? 'flex-start' : 'flex-end' }
        }}
      >
        <Stack spacing={1} sx={{ textAlign: { xs: 'left', md: isEven ? 'left' : 'right' }, maxWidth: { xs: 1, md: '70%' } }}>
          <Typography variant="caption" color="secondary" sx={{ fontWeight: 600 }}>
            STEP {index + 1 < 10 ? `0${index + 1}` : index + 1}
          </Typography>
          <Typography variant="h4" color="primary">
            {item.title}
          </Typography>
          <Typography sx={{ pb: { xs: 4, md: 0 }, color: 'secondary.light', ...theme.applyStyles('dark', { color: 'secondary.main' }) }}>
            {item.description}
          </Typography>
        </Stack>
      </TimelineContent>
    </MotionTimelineItem>
  );
}

// ==============================|| AI PROMPTS - WORKFLOW ||============================== //

export default function Workflow() {
  const theme = useTheme();
  const matchDownMd = useMediaQuery(theme.breakpoints.down('md'));

  return (
    <Box sx={{ bgcolor: 'grey.900', ...theme.applyStyles('dark', { bgcolor: 'grey.100' }), py: { xs: 6, md: 10 } }}>
      <ContainerWrapper>
        <Grid container spacing={2} sx={{ justifyContent: 'center', textAlign: 'center' }}>
          <Grid size={{ xs: 12, md: 8, lg: 6 }}>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5 }}
            >
              <SectionTypeset
                caption="Process Overview"
                heading="From Prompt to Production in 5 Simple Steps"
                description="Our intelligent prompts transforms your raw ideas into production-ready code by analyzing context, structuring logic, and optimizing for your specific stack."
                headingProps={{ color: 'white' }}
                descriptionProps={{ color: 'secondary.lighter' }}
              />
            </motion.div>
          </Grid>
          <Grid size={{ xs: 12 }}>
            <Timeline position={matchDownMd ? 'right' : 'alternate'}>
              {steps.map((item, index) => (
                <WorkflowTimelineItem key={index} item={item} index={index} count={steps.length} />
              ))}
            </Timeline>
          </Grid>
        </Grid>
      </ContainerWrapper>
    </Box>
  );
}
