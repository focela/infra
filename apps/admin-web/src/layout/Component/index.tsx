import { lazy, Suspense } from 'react';

// material-ui
import Toolbar from '@mui/material/Toolbar';

// project imports
import ComponentLayoutPage from './ComponentLayout';
import ContainerWrapper from 'components/ContainerWrapper';
import Loader from 'components/Loader';
import { handlerComponentDrawer, useGetMenuMaster } from 'api/menu';

const Header = lazy(() => import('components/pages/Header'));
const Footer = lazy(() => import('components/pages/Footer'));

// ==============================|| COMPONENTS LAYOUT ||============================== //

export default function ComponentLayout() {
  const { menuMasterLoading, menuMaster } = useGetMenuMaster();
  if (menuMasterLoading) return <Loader />;

  return (
    <Suspense fallback={<Loader />}>
      <ContainerWrapper>
        <Header
          variant="component"
          enableComponentDrawer={true}
          onComponentDrawerToggle={handlerComponentDrawer}
          isComponentDrawerOpened={menuMaster.isComponentDrawerOpened}
        />
        <Toolbar sx={{ my: 2 }} />
        <ComponentLayoutPage />
      </ContainerWrapper>
      <Footer />
    </Suspense>
  );
}
