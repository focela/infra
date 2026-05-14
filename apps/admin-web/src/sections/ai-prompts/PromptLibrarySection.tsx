// material-ui
import Grid from '@mui/material/Grid';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import PromptExplorer from 'components/pages/PromptExplorer';
import SectionTypeset from 'components/pages/SectionTypeset';

// ==============================|| PROMPT LIBRARY SECTION ||============================== //

export default function PromptLibrarySection() {
  return (
    <ContainerWrapper id="prompt-library" sx={{ scrollMarginTop: '75px' }}>
      <Grid container spacing={2} sx={{ alignItems: 'center', justifyContent: 'center' }}>
        <Grid size={12}>
          <Grid container spacing={1} sx={{ mb: 4, textAlign: 'center', justifyContent: 'center' }}>
            <Grid size={{ sm: 10, md: 6 }}>
              <SectionTypeset
                caption="AI-Powered Library"
                heading="Professional Prompt Library"
                description="Streamline your development workflow with pre-built, high-performance AI prompts tailored for React and MUI users. Exlpore all the prompts below."
              />
            </Grid>
          </Grid>
        </Grid>
        <Grid size={12} sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <PromptExplorer />
        </Grid>
      </Grid>
    </ContainerWrapper>
  );
}
