<script lang="ts">
    import {goto} from "$app/navigation";
    import {toast} from "svelte-sonner";
    import {Field, Form, Sveltik} from "sveltik/src";
    import FieldError from "$lib/components/common/form/FieldError.svelte";
    import FieldLabel from "$lib/components/common/form/FieldLabel.svelte";
    import Seo from "$lib/components/common/Seo.svelte";
    import BackgroundBeams from "$lib/components/common/animation/BackgroundBeams.svelte";
    import Submit from "$lib/components/login/Submit.svelte";
    import {repo} from "$lib/repo";
    import {Eye, EyeOff, Info} from "lucide-svelte";
    import {page} from "$app/stores";

    const initialValues = {password: '', confirmPassword: ''};
    let showPassword, showConfirmPassword = false;
    const username = $page.data.username;
    const error = $page.data.error;

    const validate = (values, actions) => {
        const errors = {
            password: '',
            confirmPassword: '',
        };

        if (values.password === '') {
            errors.password = 'Password is required';
        } else if (values.confirmPassword == '') {
            errors.confirmPassword = "Confirmation is required";
        } else if (values.password !== values.confirmPassword) {
            errors.confirmPassword = "Passwords must match";
        }

        return errors;
    };

    const handleSubmit = async (res: Response) => {
        const message = await res.json();
        if (res.ok) {
            toast.success(message);
            await goto("/profile")
            location.reload();
        } else {
            toast.error(message);
        }
    };

</script>

<Seo title="Reset Password" description="Reset your password securely"/>
{#if error}
    <div class="flex w-full justify-center items-center min-h-[calc(100vh-22vh)]">
        <div class="flex items-center justify-center text-md font-medium text-gray-800 dark:text-gray-200">
            <div class="dark:bg-red-900 bg-red-400 rounded-md p-3 flex gap-x-4 items-center justify-center">
                <Info class="min-w-6 min-h-6"/>
                {error}
            </div>
        </div>
    </div>
{:else }
    <Sveltik
            validateOnBlur={false}
            validateOnChange={false}
            {initialValues}
            {validate}
            onSubmit={async (values, actions) => {
                   /* let res = await repo.newPassword(values);
                    actions.setSubmitting(false);
                    await handleSubmit(res);*/
                }}
            let:props
    >
        <Form class='flex w-full justify-center items-center min-h-[calc(100vh-22vh)]'>
            <div class='flex w-full justify-center mx-auto max-w-2xl'>
                <div class='mx-4 flex w-full flex-col rounded-md bg-slate-50 p-6 dark:bg-neutral-800 md:mt-10 relative'>
                    <div class='flex flex-col items-center'>
                        <h1 class='text-3xl font-bold text-center text-gray-800 dark:text-gray-200'>Reset Password</h1>
                        <p class='text-gray-600 dark:text-gray-400 text-center'>Resetting your password for <strong>{username}</strong></p>
                    </div>
                    <div class="z-10">
                        <div class="flex w-full flex-row">
                            <div class='flex flex-col space-y-3 w-full md:m-4 sm:w-1/2'>
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
                                                placeholder='e.g Strong password'
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
                                <div class='py-1'/>
                                <div class='flex flex-col'>
                                    <FieldLabel For='confirmPassword'>Confirm Password</FieldLabel>
                                    <div class="flex flex-row gap-x-1">
                                        <Field
                                                type={showConfirmPassword ? "text" : "password"}
                                                on:input={(e) => props.values.confirmPassword = e.target.value}
                                                on:blur={props.handleBlur}
                                                value={props.values.confirmPassword}
                                                id='confirmPassword'
                                                name='confirmPassword'
                                                placeholder='Strong password'
                                                class='w-full rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
                                        />
                                        <button type="button"
                                                class="resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white"
                                                on:click={() => showConfirmPassword = !showConfirmPassword}>
                                            {#if showConfirmPassword}
                                                <EyeOff/>
                                            {:else }
                                                <Eye/>
                                            {/if}
                                        </button>
                                    </div>
                                    <FieldError name='confirmPassword'/>
                                </div>
                            </div>
                            <div class='flex max-sm:hidden sm:w-1/2 items-center justify-center'>
                                <a href='https://concordia-groups.web.app' class='-m-1.5 p-1.5'>
                                    <img class='sm:h-28 w-auto' src='/logo.png' alt='Concordia courses logo'/>
                                </a>
                            </div>
                        </div>
                        <Submit/>
                    </div>
                    <BackgroundBeams/>
                </div>
            </div>
        </Form>
    </Sveltik>
{/if}