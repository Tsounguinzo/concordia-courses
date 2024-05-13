<script lang="ts">
    import {twMerge} from "tailwind-merge";
    import { page } from '$app/stores';

    export let href: string;
    export let name: string;

    const blueUnderlineStyle =
        'before:content before:absolute before:block before:w-full before:h-[2px] before:bottom-0 before:left-0 before:top-5 before:bg-primary-600';
</script>

<a
        {href}
        class={twMerge(
        'relative text-sm font-semibold leading-6 text-gray-900 dark:text-gray-200',
        blueUnderlineStyle,
        $page.url.pathname !== href
          ? 'before:origin-top-left before:scale-x-0 before:transition before:duration-300 before:ease-in-out before:hover:scale-x-100'
          : ''
      )}
>
    {name}
</a>