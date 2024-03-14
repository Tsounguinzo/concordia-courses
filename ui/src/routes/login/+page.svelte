<script lang="ts">
    import {createTabs} from "svelte-headlessui";
    import {twMerge} from "tailwind-merge";
    import {Field, Form, Sveltik} from "sveltik/src";
    import LoginForm from "$lib/components/login/LoginForm.svelte";
    import FieldError from "$lib/components/common/form/FieldError.svelte";
    import FieldLabel from "$lib/components/common/form/FieldLabel.svelte";
    import Submit from "$lib/components/login/Submit.svelte";
    import Seo from "$lib/components/common/Seo.svelte";
    import {repo} from "$lib/repo";
    import {toast} from "svelte-sonner";
    import Seperator from "$lib/components/common/Seperator.svelte";
    import {goto} from "$app/navigation";
    import MultiStepLoader from "$lib/components/common/loader/MultiStepLoader/MultiStepLoader.svelte";
    import BackgroundBeams from "$lib/components/common/BackgroundBeams.svelte";

    let loading = false;

    const loadingStates = [
        {
            text: 'Crafting your account'
        },
        {
            text: 'We love study hub'
        },
        {
            text: 'Join the coolest study groups around'
        },
        {
            text: 'like or dislike a review'
        },
        {
            text: 'Hang tight! Brewing some final magic'
        }
    ];

    const initialValues = {
        username: '',
        password: '',
        email: '',
    };

    const validate = (values, actions) => {
        const errors = {
            username: '',
            password: '',
            email: '',
        };

        if (values.username === '') {
            errors.username = 'Username is required';
        } else if (values.password == '') {
            errors.password = "A password is required";
        } else if ($tabs.selected === "SingUp" && values.email == '') {
            errors.email = "Your concordia email is required";
        } else if ($tabs.selected === "SingUp" && !values.email.toLowerCase().endsWith('concordia.ca')) {
            errors.email = "Your email must be a concordia email";
        }

        return errors;
    };

    const handleSubmit = async (res: Response) => {
        const message = await res.json();
        loading = false;
        if (res.ok) {
            toast.success(message);
            location.reload();
            await goto("/profile")
        } else {
            toast.error(message);
        }
    };

    const keys = ['SingIn', 'SingUp'];
    const tabs = createTabs({selected: 'SingIn'})
</script>
<Seo title="StudyHub | Login" description="Login to concordia.courses"/>
<MultiStepLoader {loadingStates} {loading} duration={1000}/>
<div class='mx-auto max-w-2xl'>
    <div use:tabs.list class='m-4 flex space-x-1 rounded-xl bg-slate-200 p-1 dark:bg-neutral-700/20'>
        {#each keys as value}
            {@const selected = $tabs.selected === value}
            <button use:tabs.tab={{ value }}
                    class={twMerge(
                            'w-full rounded-lg py-2.5 text-sm font-medium leading-5 text-gray-800',
                            'ring-white ring-opacity-60 ring-offset-2 ring-offset-gray-400 focus:outline-none',
                            selected
                                ? 'bg-white shadow'
                                : 'text-gray-700 hover:bg-white/[0.12] hover:text-gray-400 dark:text-gray-200'
                        )}>
                {value}
            </button>
        {/each}
    </div>
    <div use:tabs.panel>
        <Sveltik
                validateOnBlur={false}
                validateOnChange={false}
                {initialValues}
                {validate}
                onSubmit={async (values, actions) => {
                    let res;
                    if ($tabs.selected === "SingUp") {
                        loading = true;
                        res = await repo.signUp(values);
                    } else {
                        res = await repo.signIn(values);
                    }
                    actions.setSubmitting(false);
                    await handleSubmit(res);
                }}
                let:props
        >
            <Form>
                <div class='flex w-full justify-center'>
                    <div class='mx-4 flex w-full flex-col rounded-md bg-slate-50 p-6 dark:bg-neutral-800 md:mt-10 relative'>
                        <div class="z-10">
                            {#if $tabs.selected === "SingUp"}
                                <LoginForm {props}/>
                                <Seperator text="Continue"/>
                                <div class='flex flex-col md:m-4'>
                                    <FieldLabel For='email'>Concordia email</FieldLabel>
                                    <Field
                                            type="email"
                                            on:input={(e) => props.values.email = e.target.value}
                                            on:blur={props.handleBlur}
                                            value={props.values.email}
                                            id='email'
                                            name='email'
                                            placeholder='ends with concordia.ca'
                                            class='resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
                                    />
                                    <FieldError name='email'/>
                                </div>
                            {:else }
                                <LoginForm {props}/>
                            {/if}
                            <Submit/>
                        </div>
                        <BackgroundBeams/>
                    </div>
                </div>
            </Form>
        </Sveltik>
    </div>
</div>