'use client';

import { Link } from 'react-router-dom';

// material-ui
import { useColorScheme } from '@mui/material/styles';
import CardMedia from '@mui/material/CardMedia';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';

// project imports
import MainCard from 'components/MainCard';
import { ThemeMode } from 'config';

// assets
import AutocompleteLight from 'assets/images/all-component/autocomplete-light.png';
import AutocompleteDark from 'assets/images/all-component/autocomplete-dark.png';
import ButtonLight from 'assets/images/all-component/button-light.png';
import ButtonDark from 'assets/images/all-component/button-dark.png';
import CheckboxLight from 'assets/images/all-component/checkbox-light.png';
import CheckboxDark from 'assets/images/all-component/checkbox-dark.png';
import RadioLight from 'assets/images/all-component/radio-light.png';
import RadioDark from 'assets/images/all-component/radio-dark.png';
import RatingLight from 'assets/images/all-component/rating-light.png';
import RatingDark from 'assets/images/all-component/rating-dark.png';
import SwitchLight from 'assets/images/all-component/switch-light.png';
import SwitchDark from 'assets/images/all-component/switch-dark.png';
import SelectLight from 'assets/images/all-component/select-light.png';
import SelectDark from 'assets/images/all-component/select-dark.png';
import SliderLight from 'assets/images/all-component/slider-light.png';
import SliderDark from 'assets/images/all-component/slider-dark.png';
import TextFieldLight from 'assets/images/all-component/textfield-light.png';
import TextFieldDark from 'assets/images/all-component/textfield-dark.png';
import AvatarLight from 'assets/images/all-component/avatar-light.png';
import AvatarDark from 'assets/images/all-component/avatar-dark.png';
import BadgeLight from 'assets/images/all-component/badge-light.png';
import BadgeDark from 'assets/images/all-component/badge-dark.png';
import ChipLight from 'assets/images/all-component/chip-light.png';
import ChipDark from 'assets/images/all-component/chip-dark.png';
import ListLight from 'assets/images/all-component/list-light.png';
import ListDark from 'assets/images/all-component/list-dark.png';
import TooltipLight from 'assets/images/all-component/tooltip-light.png';
import TooltipDark from 'assets/images/all-component/tooltip-dark.png';
import TypographyLight from 'assets/images/all-component/typography-light.png';
import TypographyDark from 'assets/images/all-component/typography-dark.png';
import AlertLight from 'assets/images/all-component/alert-light.png';
import AlertDark from 'assets/images/all-component/alert-dark.png';
import SnackbarLight from 'assets/images/all-component/snackbar-light.png';
import SnackbarDark from 'assets/images/all-component/snackbar-dark.png';
import DialogLight from 'assets/images/all-component/dialog-light.png';
import DialogDark from 'assets/images/all-component/dialog-dark.png';
import ProgressLight from 'assets/images/all-component/progress-light.png';
import ProgressDark from 'assets/images/all-component/progress-dark.png';
import BreadcrumbsLight from 'assets/images/all-component/breadcrumb-light.png';
import BreadcrumbsDark from 'assets/images/all-component/breadcrumb-dark.png';
import PaginationLight from 'assets/images/all-component/pagination-light.png';
import PaginationDark from 'assets/images/all-component/pagination-dark.png';
import StepperLight from 'assets/images/all-component/stepper-light.png';
import StepperDark from 'assets/images/all-component/stepper-dark.png';
import TabsLight from 'assets/images/all-component/tabs-light.png';
import TabsDark from 'assets/images/all-component/tabs-dark.png';
import SpeedDialLight from 'assets/images/all-component/speed-dial-light.png';
import SpeedDialDark from 'assets/images/all-component/speed-dial-dark.png';
import AccordionLight from 'assets/images/all-component/accordion-light.png';
import AccordionDark from 'assets/images/all-component/accordion-dark.png';
import CardLight from 'assets/images/all-component/card-light.png';
import CardDark from 'assets/images/all-component/card-dark.png';
import ColorLight from 'assets/images/all-component/color-light.png';
import ColorDark from 'assets/images/all-component/color-dark.png';
import DateTimePickerLight from 'assets/images/all-component/date-time-light.png';
import DateTimePickerDark from 'assets/images/all-component/date-time-dark.png';
import ModalLight from 'assets/images/all-component/modal-light.png';
import ModalDark from 'assets/images/all-component/modal-dark.png';
import ShadowsLight from 'assets/images/all-component/shadows-light.png';
import ShadowsDark from 'assets/images/all-component/shadows-dark.png';
import TimeLineLight from 'assets/images/all-component/time-line-light.png';
import TimeLineDark from 'assets/images/all-component/time-line-dark.png';
import TreeViewLight from 'assets/images/all-component/tree-view-light.png';
import TreeViewDark from 'assets/images/all-component/tree-view-dark.png';

const categories = [
  {
    title: 'Inputs',
    items: [
      { href: 'autocomplete', label: 'Autocomplete', light: AutocompleteLight, dark: AutocompleteDark },
      { href: 'buttons', label: 'Button', light: ButtonLight, dark: ButtonDark },
      { href: 'checkbox', label: 'Checkbox', light: CheckboxLight, dark: CheckboxDark },
      { href: 'radio', label: 'Radio', light: RadioLight, dark: RadioDark },
      { href: 'rating', label: 'Rating', light: RatingLight, dark: RatingDark },
      { href: 'switch', label: 'Switch', light: SwitchLight, dark: SwitchDark },
      { href: 'select', label: 'Select', light: SelectLight, dark: SelectDark },
      { href: 'slider', label: 'Slider', light: SliderLight, dark: SliderDark },
      { href: 'textfield', label: 'Text Field', light: TextFieldLight, dark: TextFieldDark }
    ]
  },
  {
    title: 'Data Display',
    items: [
      { href: 'avatars', label: 'Avatar', light: AvatarLight, dark: AvatarDark },
      { href: 'badges', label: 'Badges', light: BadgeLight, dark: BadgeDark },
      { href: 'chips', label: 'Chip', light: ChipLight, dark: ChipDark },
      { href: 'lists', label: 'List', light: ListLight, dark: ListDark },
      { href: 'tooltip', label: 'Tooltip', light: TooltipLight, dark: TooltipDark },
      { href: 'typography', label: 'Typography', light: TypographyLight, dark: TypographyDark }
    ]
  },
  {
    title: 'Feedback',
    items: [
      { href: 'alert', label: 'Alert', light: AlertLight, dark: AlertDark },
      { href: 'dialogs', label: 'Dialog', light: DialogLight, dark: DialogDark },
      { href: 'progress', label: 'Progress', light: ProgressLight, dark: ProgressDark },
      { href: 'snackbar', label: 'Snackbar', light: SnackbarLight, dark: SnackbarDark }
    ]
  },
  {
    title: 'Navigation',
    items: [
      { href: 'breadcrumbs', label: 'Breadcrumbs', light: BreadcrumbsLight, dark: BreadcrumbsDark },
      { href: 'pagination', label: 'Pagination', light: PaginationLight, dark: PaginationDark },
      { href: 'speeddial', label: 'Speed Dial', light: SpeedDialLight, dark: SpeedDialDark },
      { href: 'stepper', label: 'Stepper', light: StepperLight, dark: StepperDark },
      { href: 'tabs', label: 'Tabs', light: TabsLight, dark: TabsDark }
    ]
  },
  {
    title: 'Surfaces',
    items: [
      { href: 'accordion', label: 'Accordion', light: AccordionLight, dark: AccordionDark },
      { href: 'cards', label: 'Card', light: CardLight, dark: CardDark }
    ]
  },
  {
    title: 'Utils',
    items: [
      { href: 'color', label: 'Color', light: ColorLight, dark: ColorDark },
      { href: 'date-time-picker', label: 'Date / Time Picker', light: DateTimePickerLight, dark: DateTimePickerDark },
      { href: 'modal', label: 'Modal', light: ModalLight, dark: ModalDark },
      { href: 'shadows', label: 'Shadows', light: ShadowsLight, dark: ShadowsDark },
      { href: 'timeline', label: 'Timeline', light: TimeLineLight, dark: TimeLineDark },
      { href: 'treeview', label: 'Tree View', light: TreeViewLight, dark: TreeViewDark }
    ]
  }
];

// ==============================|| COMPONENTS - ITEM CARD ||============================== //

function ItemCard({ href, label, image }: { href: string; label: string; image: string }) {
  return (
    <Grid size={{ xs: 6, sm: 4, md: 3, lg: 2.4 }}>
      <MainCard content={false}>
        <Link to={href} style={{ textDecoration: 'none' }}>
          <CardMedia component="img" image={image} alt={label} />
          <Typography variant="h5" color="secondary.dark" sx={{ textAlign: 'center', py: 1.75, bgcolor: 'grey.50' }}>
            {label}
          </Typography>
        </Link>
      </MainCard>
    </Grid>
  );
}

// ==============================|| COMPONENTS - PRESENTATION ||============================== //

export default function Components() {
  const { colorScheme } = useColorScheme();

  return (
    <Stack sx={{ gap: { xs: 3, sm: 5 } }}>
      {categories.map((category) => (
        <Stack key={category.title} sx={{ gap: { xs: 2.75, sm: 3.75 } }}>
          <Typography variant="h3">{category.title}</Typography>
          <Grid container spacing={{ xs: 1.25, sm: 3 }}>
            {category.items.map((item) => (
              <ItemCard
                key={item.href}
                href={item.href}
                label={item.label}
                image={colorScheme === ThemeMode.DARK ? item.dark || item.light : item.light}
              />
            ))}
          </Grid>
        </Stack>
      ))}
    </Stack>
  );
}
