<script>
    import {Field} from "sveltik/src";
    import FieldError from "$lib/components/common/form/FieldError.svelte";
    import FieldLabel from "$lib/components/common/form/FieldLabel.svelte";
    import {Eye, EyeOff} from "lucide-svelte";

    export let props;
    let showPassword = false;
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
            <div class="flex flex-row gap-x-1">
                <Field
                        type={showPassword ? "text" : "password"}
                        on:input={(e) => props.values.password = e.target.value}
                        on:blur={props.handleBlur}
                        value={props.values.password}
                        id='password'
                        name='password'
                        placeholder='Strong password'
                        class='w-full rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
                />
                <button type="button"
                        class="resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white"
                        on:click={() => showPassword = !showPassword}>
                    {#if showPassword}
                        <EyeOff/>
                    {:else }
                        <Eye/>
                    {/if}
                </button>
            </div>
            <FieldError name='password'/>
        </div>
    </div>
    <div class='flex max-sm:hidden sm:w-1/2 items-center justify-center'>
        <div class='-m-1.5 p-1.5'>
            <img class='sm:h-28 w-auto' src='/logo.webp' alt='Concordia courses logo'/>
        </div>
    </div>
</div>