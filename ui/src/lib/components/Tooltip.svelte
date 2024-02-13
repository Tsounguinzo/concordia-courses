<script lang="ts">
    import {writable} from "svelte/store";
    import Transition from "svelte-transition";
    import {twMerge} from "tailwind-merge";

    export let text: string;
    export let offset: { x: number; y: number } = {x: 0, y: 0};
    export let className: string = '';

    const show = writable(false);

    /*{cloneElement<any>(elem, {
        onMouseEnter: () => {
          elem.props.onMouseEnter?.();
          setShow(true);
        },
        onMouseLeave: () => {
          elem.props.onMouseLeave?.();
          setShow(false);
        },
      })}*/

</script>

<span class='relative'>
      <Transition
              show={show && !!text}
              class={twMerge(
          'absolute z-10 min-w-fit -translate-x-0 -translate-y-full rounded-md bg-white p-2 text-center text-xs font-medium text-gray-700 dark:bg-neutral-500 dark:text-gray-100',
          className
        )}
              style={{
          left: offset.x,
          top: offset.y,
        }}
              enter='transition-opacity duration-200'
              enterFrom='opacity-0'
              enterTo='opacity-100'
              leave='transition-opacity duration-200'
              leaveFrom='opacity-100'
              leaveTo='opacity-0'
      >
        <div>{text}</div>
      </Transition>
    <slot/>
</span>