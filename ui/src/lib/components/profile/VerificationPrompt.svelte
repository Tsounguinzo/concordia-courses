<script>
    import {twMerge} from "tailwind-merge";
    import {Field, Form, Sveltik} from "sveltik/src";
    import {Check} from "lucide-svelte";
    import FieldError from "$lib/components/common/form/FieldError.svelte";
    import {toast} from "svelte-sonner";

    const validate = (values, actions) => {
        const errors = {
            token: '',
        };

        if (!values.token.match(/^\d{6}$/)) {
            errors.token = 'Code must be 6 digits';
        }

        return errors;
    };
</script>

<div class='m-4 flex justify-center items-center space-x-1 rounded-xl bg-slate-200 p-5 dark:bg-neutral-700/20'>
    <div class={twMerge( 'w-full rounded-lg py-2.5 text-sm sm:text-lg font-medium leading-5 text-gray-800',
                          'ring-white ring-opacity-60 ring-offset-2 ring-offset-gray-400 focus:outline-none',
                          'text-gray-700 dark:text-gray-200'
                        )}>
        Verify your account to proceed
    </div>
    <Sveltik
            validateOnBlur={false}
            validateOnChange={false}
            initialValues={{token: ''}}
            {validate}
            onSubmit={(values, actions) => {
                actions.setSubmitting(false);
                toast.message('submitted : ' + values.token)
            }}
            let:props
    >

        <Form>
            <div class='flex flex-col'>
                <FieldError name='token'/>
                <div class="flex flex-row gap-x-1">
                    <Field
                            on:input={(e) => props.values.token = e.target.value}
                            on:blur={props.handleBlur}
                            value={props.values.token}
                            id='token'
                            name='token'
                            placeholder='code'
                            class='w-full rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white'
                    />
                    <button type="submit" class="resize-none rounded-md border bg-gray-50 p-3 outline-none dark:border-neutral-600 dark:bg-neutral-700 dark:text-gray-200 dark:caret-white">
                        <Check/>
                    </button>
                </div>
                <button type="button" class="text-gray-600 hover:text-blue-200 text-sm sm:text-base underline">Resend verification code</button>
            </div>
        </Form>
    </Sveltik>
</div>