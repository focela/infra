// project imports
import ComponentHeader from 'components/cards/ComponentHeader';
import Components from 'sections/components-overview/Components';
import ComponentSkeleton from 'sections/components-overview/ComponentSkeleton';
import ComponentWrapper from 'sections/components-overview/ComponentWrapper';

// ==============================|| COMPONENTS - PRESENTATION ||============================== //

export default function ComponentsPresentation() {
  return (
    <ComponentSkeleton>
      <ComponentHeader
        title="All Component"
        caption="With huge resource pack making deployment easy and expanding more effectively"
        directory="src/pages/components-overview/all-components"
        link="https://mui.com/material-ui/all-components/"
      />
      <ComponentWrapper>
        <Components />
      </ComponentWrapper>
    </ComponentSkeleton>
  );
}
