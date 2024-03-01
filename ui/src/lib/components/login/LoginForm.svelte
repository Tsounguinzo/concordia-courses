<script>
    import {Field} from "sveltik/src";
    import FieldError from "$lib/components/Course/Review/FieldError.svelte";
    import FieldLabel from "$lib/components/Course/Review/FieldLabel.svelte";

    export let props;
</script>
<div class="flex w-full flex-row">
    <div class='flex flex-col space-y-3 w-full md:m-4 sm:w-1/2'>
        <div class='flex flex-col'>
            <FieldLabel For='username'>Username</FieldLabel>
            <Field
                    on:input={(e) => props.values.username = e.target.value}
                    on:blur={props.handleBlur}
                    value={props.values.username}
                    id='username'
                    name='username'
                    placeholder='e.g Beaudelaire'
                    class='resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
            />
            <FieldError name='username'/>
        </div>
        <div class='py-1'/>
        <div class='flex flex-col'>
            <FieldLabel For='password'>Password</FieldLabel>
            <Field
                    type="password"
                    on:input={(e) => props.values.password = e.target.value}
                    on:blur={props.handleBlur}
                    value={props.values.password}
                    id='password'
                    name='password'
                    placeholder='Strong password'
                    class='resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
            />
            <FieldError name='password'/>
        </div>
    </div>
    <div class='flex max-sm:hidden sm:w-1/2 items-center justify-center'>
        <a href='https://concordia-groups.web.app' class='-m-1.5 p-1.5'>
            <img class='md:h-28 w-auto' src='/favicon.png' alt='Study Hub'/>
        </a>
    </div>
</div>