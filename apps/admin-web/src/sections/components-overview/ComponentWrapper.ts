// material-ui
import { styled } from '@mui/material/styles';

// ==============================|| COMPONENTS - CONTENT WRAPPER ||============================== //

const ComponentWrapper = styled('div')(({ theme }) => ({
  paddingTop: theme.spacing(3),
  [theme.breakpoints.down('md')]: {
    paddingTop: theme.spacing(2)
  },
  [theme.breakpoints.down('sm')]: {
    paddingTop: theme.spacing(1.5)
  }
}));

export default ComponentWrapper;
