// third-party
import { ReactCompareSlider, ReactCompareSliderImage, ReactCompareSliderHandle } from 'react-compare-slider';

// material-ui
import { useTheme } from '@mui/material/styles';
import Box from '@mui/material/Box';

// project imports
import ContainerWrapper from 'components/ContainerWrapper';
import useConfig from 'hooks/useConfig';
import { getImageUrl, ImagePath } from 'utils/getImageUrl';
import { withAlpha } from 'utils/colorUtils';

// ==============================|| LANDING - BROWSER  PAGE ||============================== //

export default function BrowserBlockPage() {
  const theme = useTheme();
  const { state } = useConfig();

  return (
    <ContainerWrapper>
      <Box
        sx={(theme) => ({
          my: '5%',
          position: 'relative',
          '& .ReactCompareSlider': {
            direction: theme.direction,
            overflow: 'visible !important',
            '& [data-rcs="handle-container"]': { height: '110% !important', top: '-5% !important' }
          }
        })}
      >
        <ReactCompareSlider
          className="ReactCompareSlider"
          handle={
            <ReactCompareSliderHandle
              buttonStyle={{
                backdropFilter: undefined,
                background: withAlpha(theme.vars.palette.background.paper, 0.6),
                border: 0,
                color: theme.vars.palette.text.primary,
                width: 40,
                height: 40
              }}
              linesStyle={{ color: theme.vars.palette.primary.main, width: 4 }}
            />
          }
          itemOne={<ReactCompareSliderImage src={getImageUrl(`${state.presetColor}-dark.webp`, ImagePath.LANDING)} alt="Dark Dashboard" />}
          itemTwo={
            <ReactCompareSliderImage src={getImageUrl(`${state.presetColor}-light.webp`, ImagePath.LANDING)} alt="Light Dashboard" />
          }
        />
      </Box>
    </ContainerWrapper>
  );
}
