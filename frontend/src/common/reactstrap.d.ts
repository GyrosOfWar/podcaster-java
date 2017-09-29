declare module "reactstrap" {

    interface AlertProps {
        className?: string;
        color?: string; // default: 'success'
        isOpen?: boolean;  // default: true
        toggle?: Function;
        tag?: Function | string;

        // Set any of the timeouts to 0 to disable animation
        transitionAppearTimeout?: number;
        transitionEnterTimeout?: number;
        transitionLeaveTimeout?: number;
    }

    export class Alert extends React.Component<AlertProps, {}> {

    }

    export class UncontrolledAlert extends React.Component<AlertProps, {}> {

    }

    interface BadgeProps {
        pill?: any;
        color?: string;
    }

    export class Badge extends React.Component<BadgeProps, {}> {

    }

    interface BaseCardProps {
        tag?: Function | string;
        className?: string;
    }

    interface CardProps extends BaseCardProps {
        inverse?: boolean;
        color?: string;
        block?: boolean;
    }

    export class Card extends React.Component<CardProps, {}> {

    }

    export class CardBlock extends React.Component<BaseCardProps, {}> {

    }

    export class CardColumns extends React.Component<BaseCardProps, {}> {

    }

    export class CardDeck extends React.Component<BaseCardProps, {}> {

    }

    export class CardFooter extends React.Component<BaseCardProps, {}> {

    }

    export class CardGroup extends React.Component<BaseCardProps, {}> {

    }

    export class CardHeader extends React.Component<BaseCardProps, {}> {

    }

    export interface CardImgProps extends BaseCardProps {
        top?: boolean;
        bottom?: boolean;
    }

    export class CardImg extends React.Component<CardImgProps, {}> {

    }

    export class CardImgOverlay extends React.Component<BaseCardProps, {}> {

    }

    export class CardLink extends React.Component<BaseCardProps, {}> {

    }

    export class CardSubtitle extends React.Component<BaseCardProps, {}> {

    }

    export class CardText extends React.Component<BaseCardProps, {}> {

    }

    export class CardTitle extends React.Component<BaseCardProps, {}> {

    }

    type ColumnTypes = string | number | boolean | {
        size?: string
        push?: string | number
        pull?: string | number
        offset?: string | number
    };

    interface ColProps {
        xs?: ColumnTypes;
        sm?: ColumnTypes;
        md?: ColumnTypes;
        lg?: ColumnTypes;
        xl?: ColumnTypes;
        widths?: any[];
    }

    export class Col extends React.Component<ColProps, {}> {

    }

    interface CollapseProps extends React.HTMLProps<HTMLDivElement> {
        isOpen: boolean;
        navbar?: boolean;
        delay?: number;
        onOpened?: Function;
        onClosed?: Function;
    }

    export class Collapse extends React.Component<CollapseProps, {}> {

    }

    interface ContainerProps {
        fluid?: any;
    }

    export class Container extends React.Component<ContainerProps, {}> {

    }

    interface DropdownProps {
        disabled?: boolean;
        dropup?: boolean;
        group?: boolean;
        isOpen: boolean;
        tag?: string; // default: 'div'
        tether?: any;
        toggle?: Function;
        caret?: any;
    }

    export class Dropdown extends React.Component<DropdownProps, {}> {

    }

    interface UncontrolledDropdownProps {
        disabled?: boolean;
        dropup?: boolean;
        group?: boolean;
        isOpen?: boolean;
        tag?: string; // default: 'div'
        tether?: any;
        toggle?: Function;
        caret?: any;
    }

    export class UncontrolledDropdown extends React.Component<UncontrolledDropdownProps, {}> {

    }

    interface DropdownToggleProps {
        caret?: boolean;
        color?: string;
        className?: string;
        disabled?: boolean;
        onClick?: Function;
        "data-toggle"?: string;
        "aria-haspopup"?: boolean;
        // For DropdownToggle usage inside a Nav
        nav?: boolean;
        // Defaults to Button component
        tag?: any;
        size?: string;
    }

    export class DropdownToggle extends React.Component<DropdownToggleProps, {}> {

    }

    interface DropdownMenuProps {
        right?: any;
    }

    export class DropdownMenu extends React.Component<DropdownMenuProps, {}> {

    }

    interface DropdownItemProps {
        header?: any;
        disabled?: any;
        divider?: any;
        onClick?: Function;
    }

    export class DropdownItem extends React.Component<DropdownItemProps, {}> {

    }

    interface FormProps {
        inline?: any;
    }

    export class Form extends React.Component<FormProps, {}> {

    }

    interface FormGroupProps {
        row?: any;
        check?: any;
        disabled?: any;
        color?: string;
        tag?: Function | string;
    }

    export class FormGroup extends React.Component<FormGroupProps, {}> {

    }

    interface LabelProps {
        for?: string;
    }

    export class Label extends React.Component<LabelProps, {}> {

    }

    interface InputProps extends React.HTMLProps<HTMLInputElement> {
        type?: string;
        name?: string;
        id?: string;
        multiple?: any;
        placeholder?: string;
        state?: string;
    }

    export class Input extends React.Component<InputProps, {}> {

    }

    interface InputGroupProps {
        tag?: Function | string;
        size?: string;
        className?: string;
        style?: any;
    }

    export class InputGroup extends React.Component<InputGroupProps, {}> {

    }

    interface InputGroupAddOnProps {
        tag?: Function | string;
        className?: string;
    }

    export class InputGroupAddon extends React.Component<InputGroupAddOnProps, {}> {

    }

    interface InputGroupButtonProps {
        tag?: Function | string;
        groupClassName?: string; // only used in shorthand
        groupAttributes?: any; // only used in shorthand
        className?: string;
    }

    export class InputGroupButton extends React.Component<InputGroupButtonProps, {}> {

    }

    interface FormTextProps {
        color?: string;
    }

    export class FormText extends React.Component<FormTextProps, {}> {

    }

    interface JumbotronProps {
        tag?: Function | string;
        fluid?: boolean;
        className?: string;
    }

    export class Jumbotron extends React.Component<JumbotronProps, {}> {

    }

    export class ListGroup extends React.Component<void, {}> {

    }

    interface ListGroupItemProps {
        color?: string;
        disabled?: any;
        active?: any;
        action?: any;
        tag?: Function | string;
        to?: string; // For react-router Link elements
        href?: string;
    }

    export class ListGroupItem extends React.Component<void, {}> {

    }

    interface MediaProps {
        body?: boolean;
        bottom?: boolean;
        children?: boolean;
        className?: string;
        heading?: boolean;
        left?: boolean;
        list?: boolean;
        middle?: boolean;
        object?: boolean;
        right?: boolean;
        tag?: Function | string;
        top?: boolean;
        href?: string;
        to?: string; // For react-router Link elements
        placeholder?: any;
        image?: any;
    }

    export class Media extends React.Component<MediaProps, {}> {

    }

    interface ModalProps {
        isOpen: boolean;
        // boolean to control the state of the popover
        toggle: Function;
        // callback for toggling isOpen in the controlling component
        size?: string;
        // control backdrop, see http://v4-alpha.getbootstrap.com/components/modal/#options
        backdrop?: boolean | "static";
        keyboard?: boolean;
        // zIndex defaults to 1000.
        zIndex?: number | string;
    }

    export class Modal extends React.Component<ModalProps, {}> {

    }

    export class ModalHeader extends React.Component<React.HTMLProps<HTMLDivElement>, {}> {

    }

    export class ModalFooter extends React.Component<React.HTMLProps<HTMLDivElement>, {}> {

    }

    export class ModalBody extends React.Component<React.HTMLProps<HTMLDivElement>, {}> {

    }

    interface NavbarProps extends React.HTMLProps<HTMLDivElement> {
        light?: boolean;
        inverse?: boolean;
        full?: boolean;
        fixed?: string;
        color?: string;
        role?: string;
        toggleable?: boolean | string;
        tag?: Function | string;
    }

    export class Navbar extends React.Component<NavbarProps, {}> {

    }

    interface NavbarTogglerProps extends React.HTMLProps<HTMLDivElement> {
        type?: string;
        right?: boolean;
        left?: boolean;
        tag?: Function | string;
    }

    export class NavbarToggler extends React.Component<NavbarTogglerProps, {}> {

    }

    interface NavbarBrandProps extends React.HTMLProps<HTMLDivElement> {
        tag?: Function | string;
        to?: string;
    }

    export class NavbarBrand extends React.Component<NavbarBrandProps, {}> {

    }

    interface NavProps extends React.HTMLProps<HTMLDivElement> {
        inline?: boolean;
        disabled?: boolean;
        tabs?: boolean;
        pills?: boolean;
        stacked?: boolean;
        navbar?: boolean;
        tag?: Function | string;
    }

    export class Nav extends React.Component<NavProps, {}> {

    }

    interface NavItemProps extends React.HTMLProps<HTMLDivElement> {
        tag?: Function | string;
        to?: string;
    }

    export class NavItem extends React.Component<NavItemProps, {}> {

    }

    interface NavLinkProps extends React.HTMLProps<HTMLDivElement> {
        disabled?: boolean;
        active?: boolean;
        tag?: Function | string;
        to?: string; // For react-router Link elements
    }

    export class NavLink extends React.Component<NavLinkProps, {}> {

    }

    interface PaginationProps {
        size?: string;
    }

    export class Pagination extends React.Component<PaginationProps, {}> {

    }

    interface PaginationItemProps {
        active?: any;
        disabled?: any;
    }

    export class PaginationItem {

    }

    interface PaginationLinkProps {
        previous?: any;
        next?: any;
        href?: string;
        tag?: Function | string; // TODO: Double check that this actually exists?
        to?: string; // For react-router Link elements
    }

    export class PaginationLink extends React.Component<PaginationLinkProps, {}> {

    }

    interface PopoverProps extends React.HTMLProps<HTMLDivElement> {
        isOpen?: boolean;
        // boolean to control the state of the popover
        toggle?: Function;
        // callback for toggling isOpen in the controlling component
        target: string;
        // target div ID, popover is attached to this element
        tether?: any;
        // optionally overide tether config http://tether.io/#options
        tetherRef?: Function;
        // function which is passed a reference to the instance of tether for manually `position()`ing
        placement?: "top" |
        "bottom" |
        "left" |
        "right" |
        "top left" |
        "top center" |
        "top right" |
        "right top" |
        "right middle" |
        "right bottom" |
        "bottom right" |
        "bottom center" |
        "bottom left" |
        "left top" |
        "left middle" |
        "left bottom";
    }

    export class Popover extends React.Component<PopoverProps, {}> {

    }

    interface PopoverTitleProps extends React.HTMLProps<HTMLDivElement> {

    }

    export class PopoverTitle extends React.Component<PopoverTitle, {}> {

    }

    interface PopoverContentProps extends React.HTMLProps<HTMLDivElement> {

    }

    export class PopoverContent extends React.Component<PopoverContentProps, {}> {

    }

    interface ProgressProps {
        multi?: boolean;
        bar?: boolean;
        tag?: string; // default 'progress'
        value?: string | number; // default 0
        max?: string | number; // default 100
        animated?: boolean;
        striped?: boolean; // Typo in docs says 'stripped', huehuehue
        color?: string;
        className?: string;
    }

    export class Progress extends React.Component<ProgressProps, {}> {

    }

    export class Row extends React.Component<void, {}> {

    }

    interface TableProps {
        tag?: Function | string;
        size?: string;
        bordered?: boolean;
        striped?: boolean;
        inverse?: boolean;
        hover?: boolean;
        reflow?: boolean;
        responsive?: boolean;
    }

    export class Table extends React.Component<TableProps, {}> {

    }

    interface TabContentProps extends React.HTMLProps<HTMLDivElement> {
        activeTab?: number | string;
    }

    export class TabContent extends React.Component<TabContentProps, {}> {

    }

    interface TabPaneProps extends React.HTMLProps<HTMLDivElement> {
        tabId?: number | string;
    }

    export class TabPane extends React.Component<TabPaneProps, {}> {

    }

    interface TooltipProps {
        isOpen?: boolean;
        // boolean to control the state of the tooltip
        toggle?: Function;
        // callback for toggling isOpen in the controlling component
        target?: string;
        // target div ID, popover is attached to this element
        tether?: any | boolean;
        // optionally overide tether config http://tether.io/#options
        tetherRef?: Function;
        // function which is passed a reference to the instance of tether for manually `position()`ing
        delay?: { show?: number, hide?: number } | number;
        // optionally override show/hide delays - default { show: 0, hide: 250 }
        autohide?: boolean;
        // optionally hide tooltip when hovering over tooltip content - default true
        placement?: "top" |
        "bottom" |
        "left" |
        "right" |
        "top left" |
        "top center" |
        "top right" |
        "right top" |
        "right middle" |
        "right bottom" |
        "bottom right" |
        "bottom center" |
        "bottom left" |
        "left top" |
        "left middle" |
        "left bottom";
    }

    export class Tooltip extends React.Component<TooltipProps, {}> {

    }

    export class UncontrolledTooltip extends React.Component<TooltipProps, {}> {

    }
}
